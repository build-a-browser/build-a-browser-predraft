package net.buildabrowser.babbrowser.browser.uistate;

import java.io.Closeable;
import java.net.URI;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.imp.TabImp;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public interface Tab extends Closeable {
  
  String getTitle();
  
  Frame getFrame();
  
  URI getURL();
  
  void navigate(URI url);
  
  void reload();
  
  void back();
  
  void forward();
  
  void addTabMutationEventListener(TabMutationEventListener mutationListener, boolean sync);
  
  void removeTabMutationEventListener(TabMutationEventListener mutationListener);

  static Tab create(BrowserInstance browserInstance) {
    return new TabImp(browserInstance);
  }

}
