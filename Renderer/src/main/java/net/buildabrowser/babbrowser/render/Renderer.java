package net.buildabrowser.babbrowser.render;

import java.awt.Component;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.render.imp.RendererImp;
import net.buildabrowser.babbrowser.render.paint.Painter;

public interface Renderer {
  
  Component render() throws IOException;

  Optional<String> getTitle();

  void close();

  public static Renderer create(FetchEngine fetchEngine, URI url, Painter painter) {
    return new RendererImp(fetchEngine, url, painter);
  }

}
