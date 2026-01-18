package net.buildabrowser.babbrowser.browser.uistate.imp;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.render.uistate.Frame;
import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;

public class TabImp implements Tab {
  
  private final Frame frame;

  private final BrowserEventDispatcher<TabMutationEventListener> mutationEventDispatcher = BrowserEventDispatcher.create();

  public TabImp(BrowserInstance browserInstance) {
    this.frame = browserInstance.getRenderingEngine().createFrame();
  }

  @Override
  public void close() {
    this.frame.close();
  }
  
  @Override
  public Frame getFrame() {
    return this.frame;
  }

  @Override
  public String getName() {
    String frameName = frame.getName();
    if (frameName == null || frameName.isEmpty()) {
      return frame.getURL().toString();
    }
    return frame.getName();
  }

  @Override
  public URL getURL() {
    return frame.getURL();
  }

  @Override
  public void navigate(URL url) {
    frame.navigate(url);
    mutationEventDispatcher.fire(l -> l.onNavigate(this, url));
  }

  @Override
  public void reload() {
    frame.reload();
  }

  @Override
  public void back() {
    frame.back();
  }

  @Override
  public void forward() {
    frame.forward();
  }

  @Override
  public void addTabMutationEventListener(TabMutationEventListener mutationListener, boolean sync) {
    mutationEventDispatcher.addListener(mutationListener);
    if (sync) {
      mutationListener.onNavigate(this, getURL());
    }
  }

  @Override
  public void removeTabMutationEventListener(TabMutationEventListener mutationListener) {
    mutationEventDispatcher.removeListener(mutationListener);
  }

}