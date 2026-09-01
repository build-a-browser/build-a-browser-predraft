package net.buildabrowser.babbrowser.renderer.imp;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.html.util.DownloadUtil;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoader;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public class UANavigableOptionsImp implements UANavigableOptions {

  private final List<Runnable> repaintListeners = new LinkedList<>();

  private final UAUIFeatures uaUIFeatures;
  private final FetchEngine fetchEngine;
  private final Supplier<ExecutorService> threadGroupSupplier;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final RenderingEngine renderingEngine;
  private final DocumentRendererEventListener eventListener;
  private final SlotFamilyFamily slotFamilyFamily;

  public UANavigableOptionsImp(
    FetchEngine fetchEngine,
    Supplier<ExecutorService> threadGroupSupplier,
    DocumentLoaderRegistry documentLoaderRegistry,
    RenderingEngine renderingEngine,
    DocumentRendererEventListener eventListener,
    UAUIFeatures uaUIFeatures,
    SlotFamilyFamily slotFamilyFamily
  ) {
    this.fetchEngine = fetchEngine;
    this.threadGroupSupplier = threadGroupSupplier;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.renderingEngine = renderingEngine;
    this.eventListener = eventListener;
    this.uaUIFeatures = uaUIFeatures;
    this.slotFamilyFamily = slotFamilyFamily;
  }

  @Override
  public FetchEngine fetchEngine() {
    return this.fetchEngine;
  }

  @Override
  public ExecutorService createThreadGroup() {
    return threadGroupSupplier.get();
  }

  @Override
  public RenderableDocument loadDocument(NavigationParams navigationParams) {
    // TODO: Use the correct mime
    DocumentLoader documentLoader = documentLoaderRegistry.getByMimeType(
      navigationParams.response().headerList().get("Content-Type"));
    if (documentLoader == null) {
      DownloadUtil.handleAsDownload(
        navigationParams.response(),
        navigationParams.navigable(),
        null, // TODO: Set the ID
        null);
      return null;
    }

    RenderableDocument document = documentLoader.load(
      this, renderingEngine, navigationParams, slotFamilyFamily);
    requestRepaint();
    return document;
  }

  @Override
  public DocumentRendererEventListener eventListener() {
    return this.eventListener;
  }

  @Override
  public UAUIFeatures uiFeatures() {
    return this.uaUIFeatures;
  }

  @Override
  public void requestRepaint() {
    for (Runnable listener: repaintListeners) {
      listener.run();
    }
  }

  @Override
  public void addRepaintListener(Runnable listener) {
    repaintListeners.add(listener);
  }

  @Override
  public void removeRepaintListener(Runnable listener) {
    repaintListeners.remove(listener);
  }

  @Override
  public void onNavigate(URI url) {
    eventListener.onNavigate(url);
  }
  
}
