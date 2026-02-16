package net.buildabrowser.babbrowser.browser.render.uistate;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.render.Renderer;
import net.buildabrowser.babbrowser.browser.render.RenderingEngine;
import net.buildabrowser.babbrowser.browser.render.uistate.event.FrameEventListener;
import net.buildabrowser.babbrowser.browser.render.uistate.imp.FrameImp;

public interface Frame {

  Renderer getCurrentRenderer();

  String getName();

  URI getURL();

  void navigate(URI url);
  
  boolean redirect(URI url);

  void reload();

  void back();

  void forward();

  void close();

  void addEventListener(FrameEventListener listener, boolean sync);

  static Frame create(RenderingEngine renderingEngine) {
    return new FrameImp(renderingEngine);
  }
  
}