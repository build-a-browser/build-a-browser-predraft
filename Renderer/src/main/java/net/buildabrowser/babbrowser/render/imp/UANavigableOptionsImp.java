package net.buildabrowser.babbrowser.render.imp;

import java.util.LinkedList;
import java.util.List;

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
  private final StyleSheetList uaStyleSheets;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final Painter painter;

  public UANavigableOptionsImp(
    FetchEngine fetchEngine,
    StyleSheetList uaStyleSheets,
    DocumentLoaderRegistry documentLoaderRegistry,
    Painter painter
  ) {
    this.fetchEngine = fetchEngine;
    this.uaStyleSheets = uaStyleSheets;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.painter = painter;
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
    return this.uaStyleSheets;
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
  
}
