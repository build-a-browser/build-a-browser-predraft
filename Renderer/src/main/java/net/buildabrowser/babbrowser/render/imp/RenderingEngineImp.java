package net.buildabrowser.babbrowser.render.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.events.WindowEventLoop;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.util.TraversableUtil;
import net.buildabrowser.babbrowser.html.scripting.Window;
import net.buildabrowser.babbrowser.render.Renderer;
import net.buildabrowser.babbrowser.render.RenderingEngine;
import net.buildabrowser.babbrowser.render.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.uistate.Frame;

public class RenderingEngineImp implements RenderingEngine {

  private final FetchEngine fetchEngine;
  private final Painter painter;
  private final DocumentLoaderRegistry documentLoaderRegistry;
  private final StyleSheetList uaStyleSheets;

  public RenderingEngineImp(
    FetchEngine fetchEngine,
    Painter painter,
    DocumentLoaderRegistry documentLoaderRegistry,
    StyleSheetList uaStyleSheets
  ) {
    this.fetchEngine = fetchEngine;
    this.painter = painter;
    this.documentLoaderRegistry = documentLoaderRegistry;
    this.uaStyleSheets = uaStyleSheets;
  }

  @Override
  public Frame createFrame() {
    return Frame.create(this);
  }

  @Override
  public NavigableRendererPair createNavigable() {
    Navigable navigable = TraversableUtil.createNewTopLevelTraversable(new UANavigableOptionsImp(
      fetchEngine, uaStyleSheets, documentLoaderRegistry, painter));
    Renderer renderer = Renderer.create(navigable, painter);

    // TODO: Where does this code actually go?
    Window window = navigable.activeDocument().browsingContext().activeWindow();
    WindowEventLoop eventLoop = window.agent().eventLoop();
    eventLoop.runInParallel(() -> eventLoop.start());
    eventLoop.addNavigable(navigable);
    // TODO: Shutdown

    return new NavigableRendererPair(navigable, renderer);
  }
  
}
