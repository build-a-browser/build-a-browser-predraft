package net.buildabrowser.babbrowser.browser.render.imp;

import java.net.URL;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.RenderingEngine;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.browser.render.uistate.Frame;
import net.buildabrowser.babbrowser.dom.utils.CommonUtils;

public class RenderingEngineImp implements RenderingEngine {

  private final ProtocolRegistry protocolRegistery;
  private final Painter painter;

  public RenderingEngineImp(ProtocolRegistry protocolRegistry, Painter painter) {
    this.protocolRegistery = protocolRegistry;
    this.painter = painter;
  }

  @Override
  public Frame createFrame() {
    return Frame.create(this);
  }

  @Override
  public Renderer createBlankRenderer() {
    return Renderer.create(
      protocolRegistery,
      CommonUtils.rethrow(() -> URLUtil.createURL("about:blank")),
      painter);
  }

  @Override
  public void openRenderer(URL url, Frame frame, Consumer<Renderer> onOpen) {
    onOpen.accept(Renderer.create(protocolRegistery, url, painter));
  }
  
}
