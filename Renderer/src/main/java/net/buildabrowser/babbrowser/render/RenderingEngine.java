package net.buildabrowser.babbrowser.render;

import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.render.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.render.paint.backend.Painter;
import net.buildabrowser.babbrowser.render.uistate.Frame;

public interface RenderingEngine {

  Frame createFrame();

  NavigableRendererPair createNavigable(
    DocumentRendererEventListener eventListener
  );

  static <T> RenderingEngine create(
    FetchEngine fetchEngine,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    Supplier<StyleSheetList> uaStyleSheetsSupplier
  ) {
    return new RenderingEngineImp(
      fetchEngine, painter, documentLoaderRegistry, uaStyleSheetsSupplier);
  }

  static record NavigableRendererPair(
    Navigable navigable,
    GraphicalDocumentRenderer renderer
  ) {}
  
}
