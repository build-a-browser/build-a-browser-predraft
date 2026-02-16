package net.buildabrowser.babbrowser.browser.uistate;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.render.uistate.Frame;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.imp.TabImp;

public interface Tab {
  
  void close();
  
  String getName();
  
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
