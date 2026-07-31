package net.buildabrowser.babbrowser.browser.uistate;

import java.net.URI;
import java.util.UUID;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.imp.WindowSetImp;

public interface WindowSet {

  void close();
  
  void open(URI url);

  Tab openTabAfter(UUID uuid);
  
  Window[] getWindows();
  
  Window openWindow(WindowOptions options);
  
  void addWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener, boolean sync);
  
  void removeWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener);

  void addTabReference(Window window, UUID tabId);

  static WindowSet create(BrowserInstance browserInstance) {
    return new WindowSetImp(browserInstance);
  }
  
}