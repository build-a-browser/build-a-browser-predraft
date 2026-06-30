package net.buildabrowser.babbrowser.browser.uistate;

import java.io.Closeable;
import java.net.URI;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.imp.WindowSetImp;

public interface WindowSet extends Closeable {
  
  void open(URI url);
  
  Window[] getWindows();
  
  Window openWindow(WindowOptions options);
  
  void addWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener, boolean sync);
  
  void removeWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener);

  static WindowSet create(BrowserInstance browserInstance) {
    return new WindowSetImp(browserInstance);
  }
  
}