package net.buildabrowser.babbrowser.renderer;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.renderer.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.paint.backend.Painter;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public interface RenderingEngine {

  Frame createFrame();

  NavigableRendererPair createNavigable(
    DocumentRendererEventListener eventListener
  );

  static <T> RenderingEngine create(
    FetchEngine fetchEngine,
    Supplier<ExecutorService> threadGroupSupplier,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    Supplier<StyleSheetList> uaStyleSheetsSupplier
  ) {
    return new RenderingEngineImp(
      fetchEngine, threadGroupSupplier, painter,
      documentLoaderRegistry, uaStyleSheetsSupplier);
  }

  static record NavigableRendererPair(
    Navigable navigable,
    GraphicalDocumentRenderer renderer
  ) {}
  
}
