package net.buildabrowser.babbrowser.browser.render.imp;

import java.net.URI;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.RenderingEngine;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.browser.render.uistate.Frame;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchEngine;

public class RenderingEngineImp implements RenderingEngine {

  private final FetchEngine fetchEngine;
  private final Painter painter;

  public RenderingEngineImp(FetchEngine fetchEngine, Painter painter) {
    this.fetchEngine = fetchEngine;
    this.painter = painter;
  }

  @Override
  public Frame createFrame() {
    return Frame.create(this);
  }

  @Override
  public Renderer createBlankRenderer() {
    return Renderer.create(
      fetchEngine,
      CommonUtil.rethrow(() -> URLUtil.createURL("about:blank")),
      painter);
  }

  @Override
  public void openRenderer(URI url, Frame frame, Consumer<Renderer> onOpen) {
    onOpen.accept(Renderer.create(fetchEngine, url, painter));
  }
  
}
