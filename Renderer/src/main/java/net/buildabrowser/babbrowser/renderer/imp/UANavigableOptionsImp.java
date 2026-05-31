package net.buildabrowser.babbrowser.renderer.imp;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.paint.backend.Painter;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;

public class UANavigableOptionsImp implements UANavigableOptions {

  private final List<Runnable> repaintListeners = new LinkedList<>();

  private final FetchEngine fetchEngine;
  private final Supplier<ExecutorService> threadGroupSupplier;
  private final Supplier<StyleSheetList> uaStyleSheetsSupplier;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final Painter painter;
  private final DocumentRendererEventListener eventListener;

  public UANavigableOptionsImp(
    FetchEngine fetchEngine,
    Supplier<ExecutorService> threadGroupSupplier,
    Supplier<StyleSheetList> uaStyleSheetsSupplier,
    DocumentLoaderRegistry documentLoaderRegistry,
    Painter painter,
    DocumentRendererEventListener eventListener
  ) {
    this.fetchEngine = fetchEngine;
    this.threadGroupSupplier = threadGroupSupplier;
    this.uaStyleSheetsSupplier = uaStyleSheetsSupplier;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.painter = painter;
    this.eventListener = eventListener;
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
    RenderableDocument document = documentLoaderRegistry.getByMimeType("text/html").load(
      this, painter, navigationParams);
    requestRepaint();
    return document;
  }

  @Override
  public StyleSheetList uaStyleSheets() {
    return this.uaStyleSheetsSupplier.get();
  }

  @Override
  public DocumentRendererEventListener eventListener() {
    return this.eventListener;
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
  public void onNavigate(URI url) {
    eventListener.onNavigate(url);
  }
  
}
