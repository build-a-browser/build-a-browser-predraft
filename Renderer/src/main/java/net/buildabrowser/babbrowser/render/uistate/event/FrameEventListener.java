package net.buildabrowser.babbrowser.render.uistate.event;

import java.net.URI;

import net.buildabrowser.babbrowser.render.Renderer;

public interface FrameEventListener extends BrowserEventListener {

  default void onURLChange(URI url) {};
  
  default void onRendererChange(Renderer renderer) {};
  
}
