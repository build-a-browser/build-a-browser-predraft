package net.buildabrowser.babbrowser.browser.uistate;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.imp.WindowImp;

public interface Window {
  
  void close();

  boolean isPrivate();
  
  Tab[] getTabs();
  
  void addTab(Tab tab);
  
  Tab openTab();

  WindowSet relatedWindowSet();
  
  void addWindowMutationEventListener(WindowMutationEventListener mutationListener, boolean sync);
  
  void removeWindowMutationEventListener(WindowMutationEventListener mutationListener);
  
  static record WindowOptions(boolean isPrivate) {}

  static Window create(
    BrowserInstance browserInstance, WindowSet relatedWindowSet, WindowOptions options
  ) {
    return new WindowImp(browserInstance, relatedWindowSet, options);
  }
  
}
