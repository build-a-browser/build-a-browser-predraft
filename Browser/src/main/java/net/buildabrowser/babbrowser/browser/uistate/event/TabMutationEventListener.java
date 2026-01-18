package net.buildabrowser.babbrowser.browser.uistate.event;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventListener;
import net.buildabrowser.babbrowser.browser.uistate.Tab;

public interface TabMutationEventListener extends BrowserEventListener {

  default void onNavigate(Tab tab, URL url) {}
  
  default void onTitleChange(Tab tab, String name) {}
  
  default void onClose(Tab tab) {};
  
}
