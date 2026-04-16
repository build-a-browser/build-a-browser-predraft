package net.buildabrowser.babbrowser.browser.uistate.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.render.uistate.Frame;
import net.buildabrowser.babbrowser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.render.uistate.event.FrameEventListener;

public class TabImp implements Tab {
  
  private final Frame frame;

  private final BrowserEventDispatcher<TabMutationEventListener> mutationEventDispatcher = BrowserEventDispatcher.create();

  public TabImp(BrowserInstance browserInstance) {
    this.frame = browserInstance.getRenderingEngine().createFrame();

    frame.addEventListener(
      new FrameEventListener() {
        @Override
        public void onURLChange(URI url) {
          mutationEventDispatcher.fire(l -> l.onNavigate(TabImp.this, url));
        }
      },
      true);
  }

  @Override
  public void close() {
    this.frame.close();
    mutationEventDispatcher.fire(l -> l.onClose(this));
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
  public URI getURL() {
    return frame.getURL();
  }

  @Override
  public void navigate(URI url) {
    frame.navigate(url);
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