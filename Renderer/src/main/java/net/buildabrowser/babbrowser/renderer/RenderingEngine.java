package net.buildabrowser.babbrowser.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public interface RenderingEngine {

  Frame createFrame() throws IOException;

  NavigableRendererPair createNavigable(
    DocumentRendererEventListener eventListener
  ) throws IOException;

  static RenderingEngine create(
    FetchBackend fetchBackend,
    Supplier<ExecutorService> threadGroupSupplier,
    Painter painter,
    A11YProvider a11yProvider,
    DocumentLoaderRegistry documentLoaderRegistry,
    ResourceResolver resourceResolver
  ) {
    return new RenderingEngineImp(
      FetchEngine.create(fetchBackend),
      threadGroupSupplier, painter, a11yProvider,
      documentLoaderRegistry, resourceResolver);
  }

  static record NavigableRendererPair(
    Navigable navigable,
    GraphicalDocumentRenderer renderer
  ) {}

  static interface ResourceResolver {
  
    InputStream resolve(String resourceName);

  }
  
}
