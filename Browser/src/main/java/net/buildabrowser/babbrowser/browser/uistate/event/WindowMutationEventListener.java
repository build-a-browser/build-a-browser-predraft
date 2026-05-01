package net.buildabrowser.babbrowser.browser.uistate.event;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.render.uistate.event.BrowserEventListener;

public interface WindowMutationEventListener extends BrowserEventListener {

  default void onTabAdded(Window window, Tab tab) {}
  
  default void onClose(Window window) {}
  
}
