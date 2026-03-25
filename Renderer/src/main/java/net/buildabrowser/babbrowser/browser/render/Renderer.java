package net.buildabrowser.babbrowser.browser.render;

import java.awt.Component;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import net.buildabrowser.babbrowser.browser.render.imp.RendererImp;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.fetch.FetchEngine;

public interface Renderer {
  
  Component render() throws IOException;

  Optional<String> getTitle();

  void close();

  public static Renderer create(FetchEngine fetchEngine, URI url, Painter painter) {
    return new RendererImp(fetchEngine, url, painter);
  }

}
