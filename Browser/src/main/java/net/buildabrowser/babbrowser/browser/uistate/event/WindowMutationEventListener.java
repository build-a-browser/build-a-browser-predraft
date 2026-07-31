package net.buildabrowser.babbrowser.browser.uistate.event;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.renderer.uistate.event.BrowserEventListener;

public interface WindowMutationEventListener extends BrowserEventListener {

  default void onTabAdded(Window window, Tab tab, int index) {}
  
  default void onClose(Window window) {}

  default void onTabMoved(Window window, Tab tab, int oldIndex, int newIndex) {};
  
}
