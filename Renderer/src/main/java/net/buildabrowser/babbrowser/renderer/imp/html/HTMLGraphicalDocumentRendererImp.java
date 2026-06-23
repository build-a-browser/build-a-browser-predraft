package net.buildabrowser.babbrowser.renderer.imp.html;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.misc.ElementDocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxGenerator;
import net.buildabrowser.babbrowser.renderer.box.DocumentBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.context.imp.ElementContextImp;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.imp.RenderCSSMatcherContext;
import net.buildabrowser.babbrowser.renderer.imp.RenderDocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.layout.FontCache;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;
import net.buildabrowser.babbrowser.renderer.layout.StackingContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.logging.PerfLogging;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;
import net.buildabrowser.babbrowser.renderer.style.StyleGenerator;

public class HTMLGraphicalDocumentRendererImp implements GraphicalDocumentRenderer {

  // TODO: Allow specifying the FragmentFactory when instantiating the RenderingEngine instance
  private final FragmentFactory fragmentFactory = FragmentFactory.createDefault();

  private final HTMLDocument document;
  private final Navigable navigable;
  private final Painter painter;

  private final BoxGenerator boxGenerator;
  private final StyleSheetList uaStyleSheets;
  private final CSSMatcher cssMatcher;
  private final DocumentBox documentBox;
  private final ScriptingContext scriptingContext;
  private final DocumentChangeListener changeListener;
  private final ImageCache imageCache;
  private final SlotFamily<HTMLElement, ElementContext> elementContexts;
  private final HTMLCompositeLayers compositeLayers;
  private final HTMLEventForwardingTarget eventForwardingTarget;

  private volatile InvalidationLevel invalidationLevel = InvalidationLevel.BOX;
  private LoadedFont rootFont;

  // TODO: Switch to AtomicInteger? Synchronize?
  private int width, height;
  private boolean forceRestyle;

  public HTMLGraphicalDocumentRendererImp(
    HTMLDocument document,
    Navigable navigable,
    Painter painter,
    SlotFamilyFamily slotFamilyFamily
  ) {
    this.document = document;
    this.navigable = navigable;
    this.painter = painter;

    this.elementContexts = slotFamilyFamily.createSlotFamily(ElementContextImp::new);
    this.boxGenerator = BoxGenerator.create(elementContexts);
    this.uaStyleSheets = navigable.uaNavigableOptions().uaStyleSheets();
    this.cssMatcher = CSSMatcher.create(
      new RenderCSSMatcherContext(elementContexts),
      uaStyleSheets, slotFamilyFamily);
    this.documentBox = DocumentBox.create(document);
    this.compositeLayers = new HTMLCompositeLayers(painter);
    this.eventForwardingTarget = new HTMLEventForwardingTarget(
      document, compositeLayers, elementContexts);

    FetchEngine fetchEngine = navigable.uaNavigableOptions().fetchEngine();
    
    DocumentChangeListener innerChangeListener = new RenderDocumentChangeListener(
      cssMatcher.documentChangeListener(), elementContexts);
    innerChangeListener = new ElementDocumentChangeListener(
      fetchEngine, innerChangeListener);
    this.changeListener = new HTMLEventDocumentChangeListener(document, innerChangeListener);
    
    this.scriptingContext = ScriptingContext.create(
      fetchEngine,
      document.browsingContext().realm().hostDefined());
    this.imageCache = ImageCache.create(scriptingContext, painter.resourceLoader());

    document.focusManager().attachContext(new HTMLFocusManagerContext(elementContexts));
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
      || forceRestyle
    ) {
      long styleStartTime = System.currentTimeMillis();
      GlobalLayoutContext globalLayoutContext = createGlobalLayoutContext();
      LayoutContext layoutContext = new LayoutContext(globalLayoutContext, rootFont);
      MediaContext mediaContext = new MediaContext(
        List.of("screen"), v -> SizingUtil.evaluateBaseSize(
          layoutContext, LayoutConstraint.AUTO, v), width, height);
      cssMatcher.applyStylesheets(document, mediaContext);
      // TODO: By making a new StyleCache every round, it prevents ActiveStyles from being used
      // between rounds, but if it was moved to a field, it might hold references to styles that
      // won't be used again
      StyleCache styleCache = StyleCache.create();
      ElementSet changedElements = cssMatcher.changedElements();
      StyleGenerator.style(
        document, styleCache, elementContexts, changedElements);
      this.forceRestyle = false;
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
      this.invalidationLevel = InvalidationLevel.PAINT;
      PerfLogging.logLayoutTime(layoutStartTime);
    }
  }

  @Override
  public void updateRendering() {
    boolean needsPaint = invalidationLevel.ordinal() <= InvalidationLevel.PAINT.ordinal();
    if (
      !needsPaint
      || documentBox.htmlBox() == null
      || width <= 0 || height <= 0
    ) return;

    long paintStartTime = System.currentTimeMillis();
    
    compositeLayers.updateRendering(width, height);
    documentBox.htmlBox().context().validate();
    this.invalidationLevel = InvalidationLevel.NONE;
    
    navigable.uaNavigableOptions().requestRepaint();

    PerfLogging.logPaintTime(paintStartTime);
  }

  @Override
  public void draw(PaintCanvas canvas) {
    long windowPaintStartTime = System.currentTimeMillis();
    compositeLayers.draw(canvas, width, height);
    PerfLogging.logWindowPaintTime(windowPaintStartTime);
  }

  @Override
  public EventForwardingTarget eventForwardingTarget() {
    return this.eventForwardingTarget;
  }

  @Override
  public void resize(int width, int height) {
    if (
      this.width == width
      && this.height == height
    ) return;
    this.width = width;
    this.height = height;
    this.forceRestyle = true;
    if (this.invalidationLevel.ordinal() > InvalidationLevel.LAYOUT.ordinal()) {
      this.invalidationLevel = InvalidationLevel.LAYOUT;
    }
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
  public void removeRepaintListener(Runnable repaintListener) {
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
    Node currentNode = document.firstChild();
    while (currentNode != null) {
      Node childNode = currentNode;
      currentNode = currentNode.nextSibling();

      if (!(childNode instanceof Element)) continue;
      child = boxGenerator.box(documentBox, childNode).get(0);
      boxGenerator.fixup(child);
    }
    if (child == null) return;
    documentBox.setChild((ElementBox) child);
  }

  private void recomputeLayout() {
    ElementBox rootBox = documentBox.htmlBox();
    if (rootBox == null) return;

    GlobalLayoutContext globalLayoutContext = createGlobalLayoutContext();

    LayoutContext layoutContext = new LayoutContext(globalLayoutContext, rootFont);
    LayoutContextGenerator.generateLayoutContexts(rootBox, layoutContext);
    ArrayDeque<ElementBox> deferredLayout = new ArrayDeque<>();

    UnmanagedBoxFragment<?> fragment = rootBox.layout(
      LayoutConstraint.of(width),
      LayoutConstraint.of(height));
    fragment.setPos(0, 0);

    StackingContextGenerator.generateStackingContextsRoot(rootBox, deferredLayout);
    rootBox.stackingContext().positionFragment(
      0, 0, fragment,
      rootBox.content()::positionLayers);
    
    while (!deferredLayout.isEmpty()) {
      ElementBox itemBox = deferredLayout.pop();
      layoutAbsolute(deferredLayout, itemBox);
    }
    
    compositeLayers.regenerate(rootBox.stackingContext());
  }

  private GlobalLayoutContext createGlobalLayoutContext() {
    ResourceLoader resourceLoader = painter.resourceLoader();
    FontLoader fontLoader = resourceLoader.fontLoader();
    FontCache fontCache = FontCache.create(fontLoader);
    this.rootFont = fontCache.load(
      new FontOptions(List.of(fontLoader.sansSerif()), 16, 400));

    Viewport viewport = new Viewport(0, 0, width, height);
    GlobalLayoutContext globalLayoutContext = new GlobalLayoutContext(
      painter.resourceLoader(), rootFont.metrics(), fontCache,
      viewport, scriptingContext, imageCache, fragmentFactory);
    return globalLayoutContext;
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
    UnmanagedBoxFragment<?> itemFragment = PositionLayout.actuallyLayoutAbsolute(
      itemBox, refWidth, refHeight, insets);
    float[] position = PositionLayout.positionAbsolute(
      insets, itemFragment, refWidth, refHeight);
    
    StackingContextGenerator.generateStackingContextsDeferred(itemBox, deferredLayout);
    ownContext.positionFragment(
      position[0], position[1], itemFragment,
      itemBox.content()::positionLayers);
  }
  
}
