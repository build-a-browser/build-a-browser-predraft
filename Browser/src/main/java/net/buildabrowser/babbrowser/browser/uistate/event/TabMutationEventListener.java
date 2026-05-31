package net.buildabrowser.babbrowser.browser.uistate.event;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.renderer.uistate.event.BrowserEventListener;

public interface TabMutationEventListener extends BrowserEventListener {

  default void onNavigate(Tab tab, URI url) {}
  
  default void onTitleChange(Tab tab, String name) {}
  
  default void onClose(Tab tab) {};
  
}
