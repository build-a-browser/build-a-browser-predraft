package net.buildabrowser.babbrowser.render.uistate.event;

import java.net.URI;

public interface FrameEventListener extends BrowserEventListener {

  default void onURLChange(URI url) {};
  
}
