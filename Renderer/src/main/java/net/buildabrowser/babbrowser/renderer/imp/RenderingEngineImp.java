package net.buildabrowser.babbrowser.renderer.imp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.util.TraversableUtil;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public class RenderingEngineImp implements RenderingEngine {

  private final SlotFamilyFamily slotFamilyFamily = SlotFamilyFamily.create();
  
  private final FetchEngine fetchEngine;
  private final Supplier<ExecutorService> threadGroupSupplier;
  private final Painter painter;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final ResourceResolver resourceResolver;
  private final ClipboardProvider<?> clipboardProvider;

  public RenderingEngineImp(
    FetchEngine fetchEngine,
    Supplier<ExecutorService> threadGroupSupplier,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    ResourceResolver resourceResolver,
    ClipboardProvider<?> clipboardProvider
  ) {
    this.fetchEngine = fetchEngine;
    this.threadGroupSupplier = threadGroupSupplier;
    this.painter = painter;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.resourceResolver = resourceResolver;
    this.clipboardProvider = clipboardProvider;
    RenderingEngineInit.init(resourceResolver);
  }

  @Override
  public Frame createFrame() {
    return Frame.create(this);
  }

  @Override
  public NavigableRendererPair createNavigable(
    DocumentRendererEventListener eventListener
  ) {
    Navigable navigable = TraversableUtil.createNewTopLevelTraversable(
      new UANavigableOptionsImp(
        fetchEngine, threadGroupSupplier, documentLoaderRegistry,
        this, eventListener, slotFamilyFamily));

    // TODO: Where does this code actually go?
    Window window = navigable.activeDocument().browsingContext().activeWindow();
    WindowEventLoop eventLoop = window.agent().eventLoop();
    eventLoop.runInParallel(() -> eventLoop.start());
    eventLoop.addNavigable(navigable);
    // TODO: Shutdown

    return new NavigableRendererPair(navigable, new DelegatingGraphicalDocumentRenderer(navigable));
  }

  @Override
  public Painter painter() {
    return this.painter;
  }
  
  @Override
  public ClipboardProvider<?> clipboardProvider() {
    return this.clipboardProvider;
  }

  @Override
  public StyleSheetList uaStyleSheets() {
    try (
      Reader reader = new InputStreamReader(
        resourceResolver.resolve("ua/ua.css"))
    ) {
      CSSTokenStreamSource source = new CSSTokenStreamSource(
        CommonUtil.rethrow(() -> new URI("about:blank")));
      return StyleSheetList.createFromReader(source, reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
}
