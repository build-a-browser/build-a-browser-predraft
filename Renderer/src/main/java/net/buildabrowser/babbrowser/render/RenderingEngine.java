package net.buildabrowser.babbrowser.render;

import java.net.URI;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.render.imp.RenderingEngineImp;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.uistate.Frame;

public interface RenderingEngine {

  Frame createFrame();

  Renderer createBlankRenderer();

  void openRenderer(URI url, Frame frame, Consumer<Renderer> onOpen);

  static RenderingEngine create(FetchEngine fetchEngine, Painter painter) {
    return new RenderingEngineImp(fetchEngine, painter);
  }
  
}
