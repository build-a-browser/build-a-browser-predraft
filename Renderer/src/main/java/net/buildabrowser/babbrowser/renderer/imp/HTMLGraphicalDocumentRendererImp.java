package net.buildabrowser.babbrowser.renderer.imp;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;

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
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxGenerator;
import net.buildabrowser.babbrowser.renderer.box.DocumentBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.composite.CompositeEventsDispatcher;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayer;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.layout.FontCache;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;
import net.buildabrowser.babbrowser.renderer.layout.StackingContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.logging.PerfLogging;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.paint.backend.Painter;
import net.buildabrowser.babbrowser.renderer.paint.backend.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.renderer.style.StyleGenerator;

public class HTMLGraphicalDocumentRendererImp implements GraphicalDocumentRenderer, EventForwardingTarget {

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
  private final ImageCache imageCache;

  private volatile InvalidationLevel invalidationLevel = InvalidationLevel.BOX;
  private CompositeLayer rootLayer;
  private LoadedFont rootFont;

  // TODO: Switch to AtomicInteger? Synchronize?
  private int width, height;

  public HTMLGraphicalDocumentRendererImp(
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
    this.imageCache = ImageCache.create(scriptingContext, painter.resourceLoader());
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
  public void draw(PaintCanvas canvas) {
    if (
      this.rootLayer == null
      || this.width <= 0
      || this.height <= 0
    ) return;

    long windowPaintStartTime = System.currentTimeMillis();
    canvas.alterPaint(p -> p.setColor(0xFFFFFFFF));
    canvas.drawBox(0, 0, width, height);
    // TODO: What if the root layer is updating internally while painting? (sync)
    int[] viewport = new int[] { 0, 0, width, height };
    canvas.mark();
    rootLayer.draw(canvas, viewport);
    canvas.unmark();
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
  public Optional<String> title() {
    String title = document.title();
    if (!title.isEmpty()) {
      return Optional.of(title);
    }

    return Optional.empty();
  }

  @Override
  public DocumentChangeListener changeListener() {
    return this.changeListener;
  }

  @Override
  public void addRepaintListener(Runnable repaintListener) {
    navigable.uaNavigableOptions().addRepaintListener(repaintListener);
  }

  @Override
  public void onDocumentInvalidated(InvalidationLevel invalidationLevel) {
    if (invalidationLevel.ordinal() < this.invalidationLevel.ordinal()) {
      this.invalidationLevel = invalidationLevel;
    }
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

    Viewport viewport = new Viewport(0, 0, width, height);
    GlobalLayoutContext globalLayoutContext = new GlobalLayoutContext(
      painter.resourceLoader(), rootFont.metrics(), fontCache,
      viewport, scriptingContext, imageCache);

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
