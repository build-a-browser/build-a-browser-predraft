package net.buildabrowser.babbrowser.renderer.imp;

import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer.DocumentRendererEventListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.util.TraversableUtil;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.paint.backend.Painter;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public class RenderingEngineImp implements RenderingEngine {

  private final FetchEngine fetchEngine;
  private final Painter painter;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final Supplier<StyleSheetList> uaStyleSheetsSupplier;

  public RenderingEngineImp(
    FetchEngine fetchEngine,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    Supplier<StyleSheetList> uaStyleSheetsSupplier
  ) {
    this.fetchEngine = fetchEngine;
    this.painter = painter;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.uaStyleSheetsSupplier = uaStyleSheetsSupplier;
  }

  @Override
  public Frame createFrame() {
    return Frame.create(this);
  }

  @Override
  public NavigableRendererPair createNavigable(
    DocumentRendererEventListener eventListener
  ) {
    Navigable navigable = TraversableUtil.createNewTopLevelTraversable(
      new UANavigableOptionsImp(
        fetchEngine, uaStyleSheetsSupplier, documentLoaderRegistry,
        painter, eventListener));

    // TODO: Where does this code actually go?
    Window window = navigable.activeDocument().browsingContext().activeWindow();
    WindowEventLoop eventLoop = window.agent().eventLoop();
    eventLoop.runInParallel(() -> eventLoop.start());
    eventLoop.addNavigable(navigable);
    // TODO: Shutdown

    return new NavigableRendererPair(navigable, new DelegatingGraphicalDocumentRenderer(navigable));
  }
  
}
