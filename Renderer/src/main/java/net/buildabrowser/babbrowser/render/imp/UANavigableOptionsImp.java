package net.buildabrowser.babbrowser.render.imp;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.render.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.render.paint.Painter;

public class UANavigableOptionsImp implements UANavigableOptions {

  private final List<Runnable> repaintListeners = new LinkedList<>();

  private final FetchEngine fetchEngine;
  private final Supplier<StyleSheetList> uaStyleSheetsSupplier;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final Painter painter;
  private final Consumer<URI> onNavigate;

  public UANavigableOptionsImp(
    FetchEngine fetchEngine,
    Supplier<StyleSheetList> uaStyleSheetsSupplier,
    DocumentLoaderRegistry documentLoaderRegistry,
    Painter painter,
    Consumer<URI> onNavigate
  ) {
    this.fetchEngine = fetchEngine;
    this.uaStyleSheetsSupplier = uaStyleSheetsSupplier;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.painter = painter;
    this.onNavigate = onNavigate;
  }

  @Override
  public FetchEngine fetchEngine() {
    return this.fetchEngine;
  }

  @Override
  public Document loadDocument(NavigationParams navigationParams) {
    // TODO: Use the correct mime
    Document document = documentLoaderRegistry.getByMimeType("text/html").load(
      this, painter, navigationParams);
    requestRepaint();
    return document;
  }

  @Override
  public StyleSheetList uaStyleSheets() {
    return this.uaStyleSheetsSupplier.get();
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
    onNavigate.accept(url);
  }
  
}
