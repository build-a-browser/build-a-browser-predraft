package net.buildabrowser.babbrowser.render.imp;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.List;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.link.LinkDocumentChangeListener;
import net.buildabrowser.babbrowser.html.misc.MetaDocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.composite.CompositeEventsDispatcher;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.event.EventContext;
import net.buildabrowser.babbrowser.render.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.layout.FontCache;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.render.layout.StackingContextGenerator;
import net.buildabrowser.babbrowser.render.logging.PerfLogging;
import net.buildabrowser.babbrowser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.render.paint.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.render.paint.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;
import net.buildabrowser.babbrowser.render.style.StyleGenerator;

public class HTMLDocumentRendererImp implements DocumentRenderer, EventForwardingTarget {

  private static final BoxGenerator boxGenerator = BoxGenerator.create();

  private final EventContext eventContext = EventContext.create();

  private final HTMLDocument document;
  private final Navigable navigable;
  private final Painter painter;

  private final StyleSheetList uaStyleSheets;
  private final CSSMatcher cssMatcher;
  private final DocumentBox documentBox;
  private final ScriptingContext scriptingContext;
  private final DocumentChangeListener changeListener;

  private DocumentRendererEventListener eventListener;

  private volatile InvalidationLevel invalidationLevel = InvalidationLevel.BOX;
  private CompositeLayer rootLayer;
  private LoadedFont rootFont;

  // TODO: Switch to AtomicInteger? Synchronize?
  private int width, height;

  public HTMLDocumentRendererImp(
    HTMLDocument document,
    Navigable navigable,
    Painter painter
  ) {
    this.document = document;
    this.navigable = navigable;
    this.painter = painter;

    this.uaStyleSheets = navigable.uaNavigableOptions().uaStyleSheets();
    this.cssMatcher = CSSMatcher.create(new RenderCSSMatcherContext(), uaStyleSheets);
    this.documentBox = DocumentBox.create(document);

    FetchEngine fetchEngine = navigable.uaNavigableOptions().fetchEngine();

    DocumentChangeListener innerChangeListener = new RenderDocumentChangeListener(
      cssMatcher.documentChangeListener());
    innerChangeListener = new LinkDocumentChangeListener(
      fetchEngine, innerChangeListener);
    this.changeListener = new MetaDocumentChangeListener(innerChangeListener);
    
    this.scriptingContext = ScriptingContext.create(
      fetchEngine,
      document.browsingContext().realm().hostDefined());
  }

  @Override
  public boolean shouldRender() {
    return
      !invalidationLevel.equals(InvalidationLevel.NONE)
      || cssMatcher.changed();
  }

  @Override
  public void recalculateStyles() {
    if (
      cssMatcher.changed()
      // If level is box, either a box was inserted or a property changed to cause that,
      // so restyle is needed regardless
      || invalidationLevel.ordinal() <= InvalidationLevel.BOX.ordinal()
    ) {
      long styleStartTime = System.currentTimeMillis();
      cssMatcher.applyStylesheets(document);
      ElementSet changedElements = cssMatcher.changedElements();
      StyleGenerator.style(document, changedElements);
      PerfLogging.logStyleTime(styleStartTime);
    }
  }

  @Override
  public void updateLayout() {
    if (invalidationLevel.ordinal() <= InvalidationLevel.BOX.ordinal()) {
      long boxStartTime = System.currentTimeMillis();
      recomputeBoxes();
      PerfLogging.logBoxTime(boxStartTime);
    }
    if (invalidationLevel.ordinal() <= InvalidationLevel.LAYOUT.ordinal()) {
      long layoutStartTime = System.currentTimeMillis();
      recomputeLayout();
      PerfLogging.logLayoutTime(layoutStartTime);
    }
  }

  @Override
  public void updateRendering() {
    boolean needsPaint = invalidationLevel.ordinal() <= InvalidationLevel.PAINT.ordinal();
    if (
      !needsPaint
      || rootLayer == null
      || width <= 0 || height <= 0
    ) return;

    long paintStartTime = System.currentTimeMillis();
    int[] viewport = new int[] { 0, 0, width, height };
    rootLayer.repaint(viewport);

    document.validate();
    this.invalidationLevel = InvalidationLevel.NONE;
    
    navigable.uaNavigableOptions().requestRepaint();

    PerfLogging.logPaintTime(paintStartTime);
  }

  @Override
  public void draw(Graphics g) {
    if (
      this.rootLayer == null
      || this.width <= 0
      || this.height <= 0
    ) return;

    long windowPaintStartTime = System.currentTimeMillis();
    g.setColor(new Color(0xFFFFFF, false));
    g.fillRect(0, 0, width, height);
    // TODO: What if the root layer is updating internally while painting? (sync)
    painter.withCanvas(g, width, height, rootLayer::draw);
    PerfLogging.logWindowPaintTime(windowPaintStartTime);
  }

  @Override
  public void resize(int width, int height) {
    if (
      this.width == width
      && this.height == height
    ) return;
    this.width = width;
    this.height = height;
    if (this.invalidationLevel.ordinal() > InvalidationLevel.LAYOUT.ordinal()) {
      this.invalidationLevel = InvalidationLevel.LAYOUT;
    }
  }

  @Override
  public void forwardEvent(RendererMouseEvent mouseEvent) {
    if (rootLayer == null) return;
    // I'm not putting this on the event loop until the spec's dispatcher runs, because
    // scroll bars need to remain responsive even while something like layout is running
    // TODO: There *was* a race condition being caused by this, but I can't consistently
    // reproduce it, so I can't debug it
    CompositeEventsDispatcher.dispatchMouseEvent(
      eventContext, rootLayer, mouseEvent,
      mouseEvent.winX(), mouseEvent.winY());
  }

  @Override
  public DocumentChangeListener changeListener() {
    return this.changeListener;
  }

  @Override
  public void onDocumentInvalidated(InvalidationLevel invalidationLevel) {
    if (invalidationLevel.ordinal() < this.invalidationLevel.ordinal()) {
      this.invalidationLevel = invalidationLevel;
    }
  }

  @Override
  public void setEventListener(DocumentRendererEventListener eventListener) {
    this.eventListener = eventListener;
  }

  @Override
  public DocumentRendererEventListener eventListener() {
    return this.eventListener;
  }

  private void recomputeBoxes() {
    Box child = null;
    for (Node childNode: document.childNodes()) {
      if (!(childNode instanceof Element)) continue;
      child = boxGenerator.box(documentBox, childNode).get(0);
      boxGenerator.fixup(child);
    }
    if (child == null) return;
    documentBox.setChild((ElementBox) child);
  }

  private void recomputeLayout() {
    ResourceLoader resourceLoader = painter.resourceLoader();
    FontLoader fontLoader = resourceLoader.fontLoader();
    FontCache fontCache = FontCache.create(fontLoader);
    this.rootFont = fontCache.load(
      new FontOptions(List.of(fontLoader.sansSerif()), 16, 400));

    ElementBox rootBox = documentBox.htmlBox();
    if (rootBox == null) return;

    GlobalLayoutContext globalLayoutContext = new GlobalLayoutContext(
      painter.resourceLoader(), rootFont.metrics(), fontCache, scriptingContext);

    LayoutContext layoutContext = new LayoutContext(globalLayoutContext, rootFont);
    LayoutContextGenerator.generateLayoutContexts(rootBox, layoutContext);
    ArrayDeque<ElementBox> deferredLayout = new ArrayDeque<>();

    UnmanagedBoxFragment fragment = rootBox.layout(
      LayoutConstraint.of(width),
      LayoutConstraint.of(height));
    fragment.setPos(0, 0);

    StackingContextGenerator.generateStackingContextsRoot(rootBox, deferredLayout);
    rootBox.stackingContext().addFragment(0, 0, fragment);
    fragment.setLayerPos(0, 0);
    rootBox.content().positionLayers(0, 0);
    
    while (!deferredLayout.isEmpty()) {
      ElementBox itemBox = deferredLayout.pop();
      layoutAbsolute(deferredLayout, itemBox);
    }
    
    this.rootLayer = rootBox.stackingContext().createLayer(painter);
  }

  private void layoutAbsolute(
    ArrayDeque<ElementBox> deferredLayout,
    ElementBox itemBox
  ) {
    // TODO: Need to use proper layout context for item
    StackingContext ownContext = itemBox.stackingContext();
    StackingContext parentContext = ownContext.parentContext();
    float[] insets = ownContext.computeInsets();
    float refWidth = parentContext.innerWidth();
    float refHeight = parentContext.innerHeight();
    UnmanagedBoxFragment itemFragment = PositionLayout.actuallyLayoutAbsolute(
      itemBox, refWidth, refHeight, insets);
    float[] position = PositionLayout.positionAbsolute(
      insets, itemFragment, refWidth, refHeight);
    
    StackingContextGenerator.generateStackingContextsDeferred(itemBox, deferredLayout);
    itemFragment.setLayerPos(position[0], position[1]);
    itemBox.content().positionLayers(position[0], position[1]);
    ownContext.addFragment(position[0], position[1], itemFragment);
  }
  
}
