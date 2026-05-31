package net.buildabrowser.babbrowser.renderer.uistate.event;

import java.net.URI;

public interface FrameEventListener extends BrowserEventListener {

  default void onTitleChange(String title) {};

  default void onURLChange(URI url) {};
  
}
