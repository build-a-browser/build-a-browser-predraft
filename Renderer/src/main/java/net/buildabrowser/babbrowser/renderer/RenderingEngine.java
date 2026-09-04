package net.buildabrowser.babbrowser.renderer;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchConfig;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.content.input.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;
import net.buildabrowser.babbrowser.renderer.uistate.FrameAPIs;

public interface RenderingEngine {

  Frame createFrame();

  NavigableRendererPair createNavigable(
    Frame frame,
    DocumentRendererEventListener eventListener
  );

  Painter painter();

  ClipboardProvider<?> clipboardProvider();

  FrameAPIs newFrameAPIs(Frame frame);

  StyleSheetList uaStyleSheets();

  static RenderingEngine create(
    FetchConfig fetchConfig,
    Supplier<ExecutorService> threadGroupSupplier,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    ResourceResolver resourceResolver,
    ClipboardProvider<?> clipboardProvider,
    Function<Frame, VirtualKeyboard> virtualKeyboardFactory,
    UAUIFeatures uaUIFeatures
  ) {
    return new RenderingEngineImp(
      FetchEngine.create(fetchConfig),
      threadGroupSupplier, painter,
      documentLoaderRegistry, resourceResolver,
      clipboardProvider, virtualKeyboardFactory,
      uaUIFeatures);
  }

  static record NavigableRendererPair(
    Navigable navigable,
    GraphicalDocumentRenderer renderer
  ) {}

  static interface ResourceResolver {
  
    InputStream resolve(String resourceName);

  }
  
}
