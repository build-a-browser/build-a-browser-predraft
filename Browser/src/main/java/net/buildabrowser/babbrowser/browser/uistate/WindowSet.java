package net.buildabrowser.babbrowser.browser.uistate;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.imp.WindowSetImp;

public interface WindowSet {

  void close();
  
  void open(URL url);
  
  Window[] getWindows();
  
  Window openWindow(WindowOptions options);
  
  void addWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener, boolean sync);
  
  void removeWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener);

  static WindowSet create(BrowserInstance browserInstance) {
    return new WindowSetImp(browserInstance);
  }
  
}