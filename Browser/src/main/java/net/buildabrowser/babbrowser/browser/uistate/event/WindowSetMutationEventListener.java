package net.buildabrowser.babbrowser.browser.uistate.event;

import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.render.uistate.event.BrowserEventListener;

public interface WindowSetMutationEventListener extends BrowserEventListener {

  default void onWindowAdded(WindowSet windowSet, Window window) {}
  
  default void onClose(WindowSet windowSet) {}
  
}
