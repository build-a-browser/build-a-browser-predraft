package net.buildabrowser.babbrowser.browser.render;

import java.net.URI;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.render.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.browser.render.uistate.Frame;
import net.buildabrowser.babbrowser.fetch.FetchEngine;

public interface RenderingEngine {

  Frame createFrame();

  Renderer createBlankRenderer();

  void openRenderer(URI url, Frame frame, Consumer<Renderer> onOpen);

  static RenderingEngine create(FetchEngine fetchEngine, Painter painter) {
    return new RenderingEngineImp(fetchEngine, painter);
  }
  
}
