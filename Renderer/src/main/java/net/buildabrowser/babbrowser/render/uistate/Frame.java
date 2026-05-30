package net.buildabrowser.babbrowser.render.uistate;

import java.net.URI;

import net.buildabrowser.babbrowser.render.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.render.RenderingEngine;
import net.buildabrowser.babbrowser.render.uistate.event.FrameEventListener;
import net.buildabrowser.babbrowser.render.uistate.imp.FrameImp;

public interface Frame {

  GraphicalDocumentRenderer getRenderer();

  String getTitle();

  URI getURL();

  void navigate(URI url);

  void reload();

  void back();

  void forward();

  void close();

  void addEventListener(FrameEventListener listener, boolean sync);

  static Frame create(RenderingEngine renderingEngine) {
    return new FrameImp(renderingEngine);
  }
  
}