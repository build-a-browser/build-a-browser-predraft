package net.buildabrowser.babbrowser.browser.imp;

import java.awt.Component;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.clipboard.AWTClipboardProvider;
import net.buildabrowser.babbrowser.browser.net.imp.FetchBackendImp;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchConfig;
import net.buildabrowser.babbrowser.fetch.FetchPolicy;
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.api.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public class BrowserInstanceImp implements BrowserInstance {

  private final RenderingEngine renderingEngine;
  private final WindowSet windowSet;

  public BrowserInstanceImp(
    URI profilePath,
    ComponentPainter<Component> painter,
    CookieStore cookieStore
  ) {
    this.windowSet = WindowSet.create(this);

    ClipboardProvider<?> clipboardProvider = new AWTClipboardProvider();

    DocumentLoaderRegistry loaderRegistry = DocumentLoaderRegistry.createDefault();
    ContentEncodingRegistry registry = ContentEncodingRegistry.createDefault();
    
    ExecutorService httpExecutorService = Executors.newWorkStealingPool(16);
    FetchBackend fetchBackend = new FetchBackendImp(registry, httpExecutorService);
    FetchConfig fetchConfig = new FetchConfig(
      fetchBackend, new FetchPolicy() {}, cookieStore);

    UAUIFeatures uaUIFeatures = new UAUIFeaturesImp(windowSet);
    RenderingEngine renderingEngine = RenderingEngine.create(
      fetchConfig,
      Executors::newVirtualThreadPerTaskExecutor,
      painter,
      loaderRegistry,
      ClassLoader.getSystemClassLoader()::getResourceAsStream,
      clipboardProvider,
      new VirtualKeyboard() {},
      uaUIFeatures);
    this.renderingEngine = renderingEngine;
  }

  @Override
  public RenderingEngine getRenderingEngine() {
    return this.renderingEngine;
  }

  @Override
  public WindowSet windowSet() {
    return this.windowSet;
  }
  
}
