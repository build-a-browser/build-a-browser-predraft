package net.buildabrowser.babbrowser.browser.render.imp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.browser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.browser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.imp.DocumentBoxImp;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.browser.render.layout.FontCache;
import net.buildabrowser.babbrowser.browser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContextGenerator;
import net.buildabrowser.babbrowser.browser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.browser.render.paint.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedFont;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.browser.render.paint.ResourceLoader;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.utils.CommonUtils;
import net.buildabrowser.babbrowser.html.Window;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;

public class DocumentRendererImp implements DocumentRenderer {

  private static final BoxGenerator boxGenerator = BoxGenerator.create();

  private final Object resizeLock = new Object();
  private final Object readyImageLock = new Object();

  private final URI url;
  private final Painter painter;
  private final ProtocolRegistry protocolRegistry;
  private final StyleSheetList uaStyleSheets;
  private final Runnable postRepaint;
  private final CSSMatcher cssMatcher;
  private final MutableDocument document;
  private final DocumentBox documentBox;

  private WindowEventLoop eventLoop;

  private boolean needsLayout = true;
  private boolean needsPaint = true;
  private CompositeLayer rootLayer;
  private LoadedFont rootFont;

  private short resizeCount;
  private int width, height;

  private BufferedImage readyImage;
  private BufferedImage activeImage;

  public DocumentRendererImp(
    URI url,
    Painter painter,
    ProtocolRegistry protocolRegistry,
    StyleSheetList uaStyleSheets,
    Runnable postRepaint
  ) {
    this.url = url;
    this.painter = painter;
    this.protocolRegistry = protocolRegistry;
    this.uaStyleSheets = uaStyleSheets;
    this.postRepaint = postRepaint;

    this.cssMatcher = CSSMatcher.create(new RenderCSSMatcherContext());

    // TODO: Really not the right way to do this
    DocumentChangeListener changeListener = new RenderDocumentChangeListener(cssMatcher.documentChangeListener());
    WindowEventLoop eventLoop = EventLoop.createWindowEventLoop();
    this.document = MutableDocument.create(changeListener, this);
    Window window = Window.create(() -> eventLoop, this.document);
    document.setBrowsingContext(BrowsingContext.create(window));
    eventLoop.addNavigable(Navigable.create(
      SessionHistoryEntry.create(DocumentState.create(document))));

    this.documentBox = createDocumentBox();

    documentBox.invalidate(InvalidationLevel.LAYOUT);
  }

  @Override
  public void start() {
    Window window = ((BrowsingContext) document.browsingContext()).window();
    this.eventLoop = window.agent().eventLoop();
    eventLoop.runInParallel(() -> eventLoop.start());
    EventLoop.queueGlobalTask(TaskSource.DOM, window, () -> CommonUtils.rethrow(() -> {
      // TODO: Switch from protocolRegistry to the new Fetch system
      try (InputStream inputStream = protocolRegistry.request(url)) {
        long time = System.currentTimeMillis();
        HTMLParser.create().parse(
          new InputStreamReader(inputStream, StandardCharsets.UTF_8),
          document);
        long elapsed = System.currentTimeMillis() - time;
        System.out.println("Num millis elapsed: " + elapsed);
      }
    }));
  }

  @Override
  public void shutdown() {
    if (this.eventLoop == null) return;
    eventLoop.shutdown();
  }

  @Override
  public boolean shouldRender() {
    return this.needsLayout || this.needsPaint;
  }

  @Override
  public void recalculateStyles() {
    cssMatcher.applyStylesheets(document, uaStyleSheets);
  }

  @Override
  public void updateLayout() {
    if (!needsLayout) return;

    // TODO: Check if boxing is needed
    Node firstNode = document.childNodes().item(0);
    if (firstNode == null) return;
    Box child = boxGenerator.box(documentBox, document.childNodes().item(0)).get(0);
    documentBox.setChild((ElementBox) child);

    ResourceLoader resourceLoader = painter.resourceLoader();
    FontLoader fontLoader = resourceLoader.fontLoader();
    FontCache fontCache = FontCache.create(fontLoader);
    this.rootFont = fontCache.load(
      new FontOptions(List.of(fontLoader.sansSerif()), 16, 400));

    ElementBox rootBox = documentBox.htmlBox();

    Object cacheKey = new Object();
    GlobalLayoutContext globalLayoutContext = new GlobalLayoutContext(
      url, painter.resourceLoader(), rootFont.metrics(), fontCache, cacheKey);

    LayoutContext layoutContext = new LayoutContext(globalLayoutContext, rootFont);
    LayoutContextGenerator.generateLayoutContexts(rootBox, layoutContext);
    ArrayDeque<ElementBox> deferredLayout = new ArrayDeque<>();

    UnmanagedBoxFragment fragment = rootBox.layout(
      LayoutConstraint.of(width),
      LayoutConstraint.of(height));
    fragment.setPos(0, 0);
    StackingContextGenerator.generateStackingContextsRoot(rootBox, deferredLayout);
    rootBox.stackingContext().addFragment(0, 0, fragment);
    rootBox.content().positionLayers(0, 0);
    
    while (!deferredLayout.isEmpty()) {
      ElementBox itemBox = deferredLayout.pop();
      layoutAbsolute(deferredLayout, itemBox);
    }
    
    this.rootLayer = rootBox.stackingContext().createLayer();
    System.gc();

    this.needsLayout = false;
    this.needsPaint = true;
  }

  @Override
  public void updateRendering() {
    if (
      !needsPaint
      || rootLayer == null
      || (this.activeImage == null && this.resizeCount == 0)
    ) return;

    // TODO: Avoid synchronization block
    synchronized (resizeLock) {
      if (this.resizeCount > 0) {
        this.resizeCount--;
        this.activeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      }
    }

    Graphics g = activeImage.getGraphics();
    g.setColor(new Color(0xFFFFFF, false));
    g.fillRect(0, 0, width, height);
    
    painter.withCanvas(g, width, height, canvas -> {
      canvas.alterPaint(p -> p.setFont(rootFont));
      rootLayer.paint(canvas);
    });
    
    g.dispose();

    this.needsPaint = false;

    // TODO: Avoid synchronization block
    synchronized (readyImageLock) {
      BufferedImage prevImage = this.readyImage;
      this.readyImage = this.activeImage;
      this.activeImage = prevImage;
    }
    postRepaint.run();
  }

  @Override
  public void withImage(Consumer<BufferedImage> func) {
    if (this.readyImage == null) return;
    synchronized (readyImageLock) {
      func.accept(readyImage);
    }
  }

  private DocumentBoxImp createDocumentBox() {
    return new DocumentBoxImp() {
      @Override
      public void invalidate(InvalidationLevel invalidationLevel) {
        switch (invalidationLevel) {
          case LAYOUT -> needsLayout = true;
          case PAINT -> needsPaint = true;
        }
      }
    };
  }

  private void layoutAbsolute(
    ArrayDeque<ElementBox> deferredLayout,
    ElementBox itemBox
  ) {
    // TODO: Need to use proper layout context for item
    StackingContext parentContext = itemBox.stackingContext().parentContext();
    float[] insets = itemBox.stackingContext().computeInsets();
    float refWidth = parentContext.computeWidth();
    float refHeight = parentContext.computeHeight();
    UnmanagedBoxFragment itemFragment = PositionLayout.actuallyLayoutAbsolute(
      itemBox, refWidth, refHeight, insets);
    float[] position = PositionLayout.positionAbsolute(
      insets, itemFragment, refWidth, refHeight);
    
    StackingContextGenerator.generateStackingContextsDeferred(itemBox, deferredLayout);
    itemBox.stackingContext().addFragment(
      parentContext.posX() + position[0],
      parentContext.posY() + position[1],
      itemFragment);
    itemBox.content().positionLayers(0, 0);
  }

  @Override
  public void resize(int width, int height) {
    synchronized (resizeLock) {
      if (
        this.width == width
        && this.height == height
      ) return;
      this.width = width;
      this.height = height;
      this.resizeCount = 2;
      this.needsLayout = true;
    }
  }
  
}
