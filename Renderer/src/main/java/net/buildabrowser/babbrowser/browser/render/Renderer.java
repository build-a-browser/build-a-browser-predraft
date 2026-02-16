package net.buildabrowser.babbrowser.browser.render;

import java.awt.Component;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.imp.RendererImp;

public interface Renderer {
  
  Component render() throws IOException;

  Optional<String> getTitle();

  public static Renderer create(ProtocolRegistry protocolRegistry, URI url) {
    return new RendererImp(protocolRegistry, url);
  }

}
