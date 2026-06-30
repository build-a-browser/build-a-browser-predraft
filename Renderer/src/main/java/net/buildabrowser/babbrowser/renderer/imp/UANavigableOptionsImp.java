package net.buildabrowser.babbrowser.renderer.imp;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoader;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public class UANavigableOptionsImp implements UANavigableOptions {

  private final List<Runnable> repaintListeners = new LinkedList<>();

  private final FetchEngine fetchEngine;
  private final Supplier<ExecutorService> threadGroupSupplier;
  private final Supplier<StyleSheetList> uaStyleSheetsSupplier;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final Painter painter;
  private final A11YProvider a11yProvider;
  private final DocumentRendererEventListener eventListener;
  private final SlotFamilyFamily slotFamilyFamily;

  public UANavigableOptionsImp(
    FetchEngine fetchEngine,
    Supplier<ExecutorService> threadGroupSupplier,
    Supplier<StyleSheetList> uaStyleSheetsSupplier,
    DocumentLoaderRegistry documentLoaderRegistry,
    Painter painter,
    A11YProvider a11yProvider,
    DocumentRendererEventListener eventListener,
    SlotFamilyFamily slotFamilyFamily
  ) {
    this.fetchEngine = fetchEngine;
    this.threadGroupSupplier = threadGroupSupplier;
    this.uaStyleSheetsSupplier = uaStyleSheetsSupplier;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.painter = painter;
    this.a11yProvider = a11yProvider;
    this.eventListener = eventListener;
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
  public RenderableDocument loadDocument(
    NavigationParams navigationParams
  ) throws IOException {
    // TODO: Use the correct mime
    DocumentLoader documentLoader = documentLoaderRegistry.getByMimeType("text/html");
    RenderableDocument document = documentLoader.load(
      this, painter, a11yProvider, navigationParams, slotFamilyFamily);
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
  public void removeRepaintListener(Runnable listener) {
    repaintListeners.remove(listener);
  }

  @Override
  public void onNavigate(URI url) {
    eventListener.onNavigate(url);
  }
  
}
