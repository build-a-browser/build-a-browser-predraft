package net.buildabrowser.babbrowser.browser.uistate.event;

import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventListener;
import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;

public interface WindowMutationEventListener extends BrowserEventListener {

  default void onTabAdded(Window window, Tab tab) {}
  
  default void onClose(Window window) {}
  
}
