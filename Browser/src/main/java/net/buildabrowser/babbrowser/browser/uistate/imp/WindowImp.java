package net.buildabrowser.babbrowser.browser.uistate.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.event.TabMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;

public class WindowImp implements Window {

  private final List<Tab> tabs = new ArrayList<>();
  private final BrowserEventDispatcher<WindowMutationEventListener> mutationEventDispatcher = BrowserEventDispatcher.create();
  private final WindowOptions options;
  private final BrowserInstance browserInstance;

  public WindowImp(BrowserInstance browserInstance, WindowOptions options) {
    this.options = options;
    this.browserInstance = browserInstance;
  }

  @Override
  public void close() {
    mutationEventDispatcher.fire(listener -> listener.onClose(this));
  }

  @Override
  public boolean isPrivate() {
    return options.isPrivate();
  }

  @Override
  public Tab[] getTabs() {
    return tabs.toArray(new Tab[tabs.size()]);
  }

  @Override
  public void addTab(Tab tab) {
    tab.addTabMutationEventListener(new TabCleanupListener(), false);
    tabs.add(tab);
    mutationEventDispatcher.fire(listener -> listener.onTabAdded(this, tab));
  }
  
  @Override
  public Tab openTab() {
    Tab tab = Tab.create(browserInstance);
    tabs.add(tab);
    mutationEventDispatcher.fire(l -> l.onTabAdded(this, tab));
    
    return tab;
  }

  @Override
  public void addWindowMutationEventListener(WindowMutationEventListener mutationListener, boolean sync) {
    mutationEventDispatcher.addListener(mutationListener);
    if (sync) {
      syncListener(mutationListener);
    }
  }

  @Override
  public void removeWindowMutationEventListener(WindowMutationEventListener mutationListener) {
    mutationEventDispatcher.removeListener(mutationListener);
  }
  
  private void syncListener(WindowMutationEventListener mutationListener) {
    for (Tab tab: tabs) {
      mutationListener.onTabAdded(this, tab);
    }
  }

  private class TabCleanupListener implements TabMutationEventListener {
    
    @Override
    public void onClose(Tab tab) {
      tabs.remove(tab);
    }
    
  }
  
}
