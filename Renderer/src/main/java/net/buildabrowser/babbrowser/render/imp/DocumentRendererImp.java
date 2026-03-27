package net.buildabrowser.babbrowser.render.imp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.UAHTMLDocumentOptions;
import net.buildabrowser.babbrowser.html.link.LinkDocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxGenerator;
import net.buildabrowser.babbrowser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.Box.InvalidationLevel;
import net.buildabrowser.babbrowser.render.box.imp.DocumentBoxImp;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.position.PositionLayout;
import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.layout.FontCache;
import net.buildabrowser.babbrowser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutContextGenerator;
import net.buildabrowser.babbrowser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.render.layout.StackingContextGenerator;
import net.buildabrowser.babbrowser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.render.paint.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;
import net.buildabrowser.babbrowser.render.paint.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStream.ReadableStreamGetReaderOptions;
import net.buildabrowser.babbrowser.stream.imp.ReadableStreamDefaultReaderImp;

public class DocumentRendererImp implements DocumentRenderer {

  private static final BoxGenerator boxGenerator = BoxGenerator.create();

  private final Object resizeLock = new Object();
  private final Object readyImageLock = new Object();

  private final URI url;
  private final Painter painter;
  private final StyleSheetList uaStyleSheets;
  private final Runnable postRepaint;
  private final CSSMatcher cssMatcher;
  private final HTMLDocument document;
  private final DocumentBox documentBox;
  private final ScriptingContext scriptingContext;

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
    FetchEngine fetchEngine,
    StyleSheetList uaStyleSheets,
    Runnable postRepaint
  ) {
    this.url = url;
    this.painter = painter;
    this.uaStyleSheets = uaStyleSheets;
    this.postRepaint = postRepaint;

    this.cssMatcher = CSSMatcher.create(new RenderCSSMatcherContext());

    DocumentChangeListener innerChangeListener = new RenderDocumentChangeListener(
      cssMatcher.documentChangeListener());
    DocumentChangeListener changeListener = new LinkDocumentChangeListener(
      fetchEngine, innerChangeListener);
    UAHTMLDocumentOptions documentOptions = new UAHTMLDocumentOptions(changeListener, this);

    BrowsingContext browsingContext = BrowsingContext.create(documentOptions);
    this.document = browsingContext.activeDocument();
    WindowEventLoop eventLoop = browsingContext.activeWindow().agent().eventLoop();
    eventLoop.addNavigable(Navigable.create(
      SessionHistoryEntry.create(DocumentState.create(document))));

    // TODO: Proper navigation
    document.setURL(url);
    
    this.scriptingContext = ScriptingContext.create(
      fetchEngine,
      browsingContext.realm().hostDefined());

    this.documentBox = createDocumentBox();

    documentBox.invalidate(InvalidationLevel.LAYOUT);
  }

  @Override
  public void start() {
    Window window = document.browsingContext().activeWindow();
    WindowEventLoop eventLoop = window.agent().eventLoop();
    eventLoop.runInParallel(() -> eventLoop.start());

    MutableFetchRequest fetchRequest = FetchRequest.createMutable();
    fetchRequest.setMethod("GET");
    fetchRequest.setURL(url);
    fetchRequest.setClient(scriptingContext.environmentSettingsObject());

    long time = System.currentTimeMillis();
    HTMLParser htmlParser = HTMLParser.create(document, StandardCharsets.UTF_8);

    FetchParameters fetchParameters = new FetchParameters();
    fetchParameters.request = fetchRequest;
    fetchParameters.processResponse = (response) -> {
      ReadableStreamGetReaderOptions options = new ReadableStreamGetReaderOptions();
      ReadableStreamDefaultReaderImp reader = (ReadableStreamDefaultReaderImp) response.body().stream().getReader(options);
      // TODO: Use the normal reader's exposed methods instead, once implemented
      reader.read(new ReadRequest() {

        @Override
        public void chunk(ByteBuffer chunk) {
          EventLoop.queueGlobalTask(TaskSource.DOM, window,
            () -> {
              htmlParser.parse(chunk);
              documentBox.invalidate(InvalidationLevel.LAYOUT);
            });
          reader.read(this);
        }

        @Override
        public void close() {
          EventLoop.queueGlobalTask(TaskSource.DOM, window, htmlParser::done);
          long elapsed = System.currentTimeMillis() - time;
          System.out.println("Num millis elapsed: " + elapsed);
        }

        @Override
        public void error(Object e) {
          ((Throwable) e).printStackTrace();
        }
        
      });
    };

    scriptingContext.fetchEngine().fetch(fetchParameters);
  }

  @Override
  public void shutdown() {
    scriptingContext.globalObject().agent().eventLoop().shutdown();
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
      url, painter.resourceLoader(), rootFont.metrics(), fontCache,
      scriptingContext, cacheKey);

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
