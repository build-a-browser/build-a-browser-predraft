package net.buildabrowser.babbrowser.browser.render.uistate.event;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.render.Renderer;

public interface FrameEventListener extends BrowserEventListener {

  default void onURLChange(URL url) {};
  
  default void onRendererChange(Renderer renderer) {};
  
}
