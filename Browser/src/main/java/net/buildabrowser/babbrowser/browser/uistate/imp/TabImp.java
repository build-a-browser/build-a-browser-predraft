package net.buildabrowser.babbrowser.browser.uistate.imp;

import java.io.IOException;
import java.net.URI;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;
import net.buildabrowser.babbrowser.renderer.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.renderer.uistate.event.FrameEventListener;

public class TabImp implements Tab {
  
  private final Frame frame;

  private final BrowserEventDispatcher<TabMutationEventListener> mutationEventDispatcher = BrowserEventDispatcher.create();

  public TabImp(BrowserInstance browserInstance) {
    this.frame = CommonUtil.rethrow(() ->
      browserInstance.getRenderingEngine().createFrame());

    frame.addEventListener(
      new FrameEventListener() {
        @Override
        public void onURLChange(URI url) {
          mutationEventDispatcher.fire(l -> l.onNavigate(TabImp.this, url));
        }

        @Override
        public void onTitleChange(String title) {
          mutationEventDispatcher.fire(l -> l.onTitleChange(TabImp.this, title));
        }
      },
      true);
  }

  @Override
  public void close() throws IOException {
    this.frame.close();
    mutationEventDispatcher.fire(l -> l.onClose(this));
  }
  
  @Override
  public Frame getFrame() {
    return this.frame;
  }

  @Override
  public String getTitle() {
    String frameName = frame.getTitle();
    if (frameName == null || frameName.isEmpty()) {
      return frame.getURL().toString();
    }
    return frameName;
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