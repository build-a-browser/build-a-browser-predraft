package net.buildabrowser.babbrowser.browser.uistate.event;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventListener;
import net.buildabrowser.babbrowser.browser.uistate.Tab;

public interface TabMutationEventListener extends BrowserEventListener {

  default void onNavigate(Tab tab, URI url) {}
  
  default void onTitleChange(Tab tab, String name) {}
  
  default void onClose(Tab tab) {};
  
}
