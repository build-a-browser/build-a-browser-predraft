package net.buildabrowser.babbrowser.browser.render;

import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.imp.RendererImp;

public interface Renderer {
  
  Component render() throws IOException;

  public static Renderer create(ProtocolRegistry protocolRegistry, URL url) {
    return new RendererImp(protocolRegistry, url);
  }

}
