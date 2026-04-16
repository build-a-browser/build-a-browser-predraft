package net.buildabrowser.babbrowser.render;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.render.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.uistate.Frame;

public interface RenderingEngine {

  Frame createFrame();

  NavigableRendererPair createNavigable(Consumer<URI> onNavigate);

  static RenderingEngine create(
    FetchEngine fetchEngine,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    Supplier<StyleSheetList> uaStyleSheetsSupplier
  ) {
    return new RenderingEngineImp(
      fetchEngine, painter, documentLoaderRegistry, uaStyleSheetsSupplier);
  }

  static record NavigableRendererPair(Navigable navigable, Renderer renderer) {}
  
}
