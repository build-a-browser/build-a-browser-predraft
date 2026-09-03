package net.buildabrowser.babbrowser.renderer.imp.html;

import java.util.List;
import java.util.Optional;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.debugger.core.DebugContext;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.handlers.ObjectLoader;
import net.buildabrowser.babbrowser.html.misc.ElementDocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.HTMLDocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.api.FrameAPIs;
import net.buildabrowser.babbrowser.renderer.api.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.box.BoxGenerator;
import net.buildabrowser.babbrowser.renderer.box.DocumentBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;
import net.buildabrowser.babbrowser.renderer.context.imp.ElementContextImp;
import net.buildabrowser.babbrowser.renderer.context.imp.FakeRootContextImp;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.imp.RenderCSSMatcherContext;
import net.buildabrowser.babbrowser.renderer.imp.RenderDocumentChangeListener;
import net.buildabrowser.babbrowser.renderer.layout.FontCache;
import net.buildabrowser.babbrowser.renderer.layout.FontWordWidthCache;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.HTMLLayout;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.logging.PerfLogging;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;
import net.buildabrowser.babbrowser.renderer.style.StyleGenerator;

public class HTMLGraphicalDocumentRendererImp implements GraphicalDocumentRenderer, HTMLDocumentRenderer {

  // TODO: Allow specifying the FragmentFactory when instantiating the RenderingEngine instance
  private final FragmentFactory fragmentFactory = FragmentFactory.createDefault();
  private final FontWordWidthCache fontWordWidthCache = FontWordWidthCache.create();
  private final StyleCache styleCache = StyleCache.create();

  private final HTMLDocument document;
  private final Navigable navigable;
  private final FrameAPIs frameAPIs;
  private final Painter painter;

  private final BoxGenerator boxGenerator;
  private final StyleSheetList uaStyleSheets;
  private final CSSMatcher cssMatcher;
  private final DocumentBox documentBox;
  private final ScriptingContext scriptingContext;
  private final DocumentChangeListener changeListener;
  private final ImageCache imageCache;
  private final FontCache fontCache;
  private final ObjectLoader objectLoader;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;
  private final FakeRootContextImp fakeRootContext;
  private final HTMLCompositeLayers compositeLayers;
  private final HTMLEventForwardingTarget eventForwardingTarget;
  private final SelectionContext selectionContext;
  private final DebugContext debugContext;

  private volatile short invalidationLevel = InvalidationLevel.BOX;
  private LoadedFont rootFont;

  // TODO: Switch to AtomicInteger? Synchronize?
  private int width, height;

  public HTMLGraphicalDocumentRendererImp(
    HTMLDocument document,
    Navigable navigable,
    RenderingEngine renderingEngine,
    FrameAPIs frameAPIs,
    SlotFamilyFamily slotFamilyFamily
  ) {
    this.document = document;
    this.navigable = navigable;
    this.frameAPIs = frameAPIs;
    this.painter = renderingEngine.painter();

    EventContext eventContext = EventContext.create();
    this.documentBox = DocumentBox.create(document);
    this.renderContexts = slotFamilyFamily.createSlotFamily(ElementContextImp::new);
    this.fakeRootContext = new FakeRootContextImp(renderContexts.familyId(), documentBox);
    this.boxGenerator = BoxGenerator.create(renderContexts);
    this.uaStyleSheets = renderingEngine.uaStyleSheets();
    this.cssMatcher = CSSMatcher.create(
      new RenderCSSMatcherContext(renderContexts),
      uaStyleSheets, slotFamilyFamily);
    this.compositeLayers = new HTMLCompositeLayers(painter);
    this.selectionContext = SelectionContext.create(
      document.getSelection(),
      // TODO: Not so great to leech off of the CSS module
      cssMatcher.allElements().createChild());
    this.debugContext = new HTMLDebugContext(
      document, documentBox, renderContexts);
    documentBox.setChild(fakeRootContext.box());

    FetchEngine fetchEngine = navigable.uaNavigableOptions().fetchEngine();
    
    DocumentChangeListener innerChangeListener = new RenderDocumentChangeListener(
      cssMatcher.documentChangeListener(), renderContexts);
    innerChangeListener = new ElementDocumentChangeListener(
      fetchEngine, innerChangeListener);
    innerChangeListener = new HTMLEventDocumentChangeListener(
      document, innerChangeListener, renderContexts);
    // TODO: The debugger listener should ideally come last (so it can cancel events)
    // but ForkedDocumentChangeListener needs modified to allow passing through fragment events
    innerChangeListener = new HTMLFragmentNavigationDocumentChangeListener(
      innerChangeListener);
    innerChangeListener = new HTMLSelectionDocumentChangeListener(
      document, selectionContext, innerChangeListener);
    this.changeListener = maybeAddDebuggerChangeListener(innerChangeListener);
    
    changeListener.onURLChanged(null, document.url());
    
    EventForwardingTarget eventForwardingTarget = new HTMLSelectionEventForwardingTarget<>(
      document, selectionContext, renderingEngine.clipboardProvider(),
      renderContexts, null);
    this.eventForwardingTarget = new HTMLEventForwardingTarget(
      eventContext, document, compositeLayers, renderContexts,
      eventForwardingTarget);

    this.scriptingContext = ScriptingContext.create(
      fetchEngine,
      document.browsingContext().realm().hostDefined());
    this.imageCache = ImageCache.create(scriptingContext, painter.resourceLoader());
    this.fontCache = FontCache.create(painter.resourceLoader().fontLoader());
    this.objectLoader = new HTMLObjectLoader(imageCache, renderContexts);

    VirtualKeyboard keyboard = frameAPIs.virtualKeyboard();
    document.focusManager().attachContext(
      new HTMLFocusManagerContext(eventContext, keyboard, renderContexts));
  }

  @Override
  public boolean shouldRender() {
    // Hackily need to place this here, because debugger might need to update any frame
    // and other methods on the event loop may not run
    updateDebugger();
    return
      invalidationLevel != InvalidationLevel.NONE
      || cssMatcher.changed();
  }

  @Override
  public void recalculateStyles() {
    if (
      cssMatcher.changed()
      // If level is box, either a box was inserted or a property changed to cause that,
      // so restyle is needed regardless
      || (invalidationLevel & InvalidationLevel.BOX) != 0
      || (invalidationLevel & InvalidationLevel.STYLE) != 0
    ) {
      long styleStartTime = System.currentTimeMillis();
      GlobalLayoutContext globalLayoutContext = createGlobalLayoutContext();
      LayoutContext layoutContext = new LayoutContext(
        globalLayoutContext, rootFont, rootFont.metrics());
      MediaContext mediaContext = new MediaContext(
        List.of("screen"), v -> SizingUtil.evaluateBaseSize(
          layoutContext, LayoutConstraint.AUTO, v), width, height);
      cssMatcher.applyStylesheets(document, mediaContext);
      StyleGenerator.style(
        document, styleCache, renderContexts);
      fakeRootContext.regenerateStyles(styleCache, null);
      PerfLogging.logStyleTime(styleStartTime);
    }
  }

  @Override
  public void updateLayout() {
    if ((invalidationLevel & InvalidationLevel.BOX) != 0) {
      long boxStartTime = System.currentTimeMillis();
      recomputeBoxes();
      PerfLogging.logBoxTime(boxStartTime);
      updateDebugger();
    }
    if ((invalidationLevel & InvalidationLevel.LAYOUT) != 0) {
      long layoutStartTime = System.currentTimeMillis();
      recomputeLayout();
      this.invalidationLevel = InvalidationLevel.PAINT;
      PerfLogging.logLayoutTime(layoutStartTime);
      updateDebugger();
    }
  }

  @Override
  public void updateRendering() {
    boolean needsPaint = invalidationLevel != 0;
    if (
      !needsPaint
      || documentBox.child() == null
      || width <= 0 || height <= 0
    ) return;

    long paintStartTime = System.currentTimeMillis();
    
    compositeLayers.updateRendering(width, height);
    documentBox.child().context().validate();
    this.invalidationLevel = InvalidationLevel.NONE;
    
    navigable.uaNavigableOptions().requestRepaint();

    PerfLogging.logPaintTime(paintStartTime);

    updateDebugger();
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
    this.invalidationLevel |= InvalidationLevel.STYLE;
    this.invalidationLevel |= InvalidationLevel.LAYOUT;
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
  public ObjectLoader objectLoader() {
    return this.objectLoader;
  }

  @Override
  public DocumentChangeListener changeListener() {
    return this.changeListener;
  }

  @Override
  public void onDocumentInvalidated(short invalidationLevel) {
    this.invalidationLevel |= invalidationLevel;
  }

  @Override
  public FrameAPIs frameAPIs() {
    return this.frameAPIs;
  }

  private void recomputeBoxes() {
    ElementBox wrapperBox = fakeRootContext.wrapperBox();
    ElementBox child = null;
    Node currentNode = document.firstChild();
    while (currentNode != null) {
      Node childNode = currentNode;
      currentNode = currentNode.nextSibling();

      if (!(childNode instanceof Element)) continue;
      child = (ElementBox) boxGenerator.box(wrapperBox, childNode).get(0);
      boxGenerator.fixup(child);
    }
    if (child == null) return;

    fakeRootContext.replaceChild(child);
    selectionContext.updateSelection();
  }

  private void recomputeLayout() {
    ElementBox rootBox = documentBox.child();
    if (rootBox == null) return;

    GlobalLayoutContext globalLayoutContext = createGlobalLayoutContext();

    LayoutContext layoutContext = new LayoutContext(
      globalLayoutContext, rootFont, rootFont.metrics());
    LayoutContextGenerator.generateLayoutContexts(rootBox, layoutContext);

    HTMLLayout.doLayout(rootBox, width, height);
    
    compositeLayers.regenerate(rootBox.stackingContext());
  }

  private GlobalLayoutContext createGlobalLayoutContext() {
    ResourceLoader resourceLoader = painter.resourceLoader();
    FontLoader fontLoader = resourceLoader.fontLoader();
    this.rootFont = fontCache.load(
      new FontOptions(List.of(fontLoader.sansSerif()), 16, 400));

    Viewport viewport = new Viewport(0, 0, width, height);
    GlobalLayoutContext globalLayoutContext = new GlobalLayoutContext(
      painter.resourceLoader(), fontCache, fontWordWidthCache,
      viewport, scriptingContext, selectionContext, imageCache, fragmentFactory);
    return globalLayoutContext;
  }

  private void updateDebugger() {
    if (
      navigable.uaNavigableOptions().eventListener()
        instanceof DebuggableDocumentRendererEventListener debuggableEventListener
    ) {
      debuggableEventListener.update(debugContext);
    }
  }

  private DocumentChangeListener maybeAddDebuggerChangeListener(
    DocumentChangeListener innerChangeListener
  ) {
    if (
      navigable.uaNavigableOptions().eventListener()
        instanceof DebuggableDocumentRendererEventListener debuggableEventListener
    ) {
      return debuggableEventListener.newChangeListener(innerChangeListener);
    }

    return innerChangeListener;
  }
  
}
