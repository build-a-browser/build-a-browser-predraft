package net.buildabrowser.babbrowser.browser.render.uistate.event;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.render.Renderer;

public interface FrameEventListener extends BrowserEventListener {

  default void onURLChange(URI url) {};
  
  default void onRendererChange(Renderer renderer) {};
  
}
