package net.buildabrowser.babbrowser.browser.uistate.imp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;

public class WindowSetImp implements WindowSet {
  
  private final BrowserInstance browserInstance;
  
  private final List<Window> windows = new ArrayList<>();
  private final BrowserEventDispatcher<WindowSetMutationEventListener> mutationEventDispatcher = BrowserEventDispatcher.create();

  public WindowSetImp(BrowserInstance browserInstance) {
    this.browserInstance = browserInstance;
  }
  
  @Override
  public void close() {
    for (Window window: windows) {
      window.close();
    }
    mutationEventDispatcher.fire(listener -> listener.onClose(this));
  }

  @Override
  public void open(URL url) {
    windows.get(0).openTab().navigate(url);
  }

  @Override
  public Window[] getWindows() {
    return windows.toArray(new Window[windows.size()]);
  }

  @Override
  public Window openWindow(WindowOptions options) {
    Window window = Window.create(browserInstance, options);
    window.addWindowMutationEventListener(new WindowCleanupListener(), false);
    windows.add(window);
    mutationEventDispatcher.fire(listener -> listener.onWindowAdded(this, window));
    
    return window;
  }

  @Override
  public void addWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener, boolean sync) {
    mutationEventDispatcher.addListener(mutationListener);
    if (sync) {
      syncListener(mutationListener);
    }
  }

  @Override
  public void removeWindowSetMutationEventListener(WindowSetMutationEventListener mutationListener) {
    mutationEventDispatcher.removeListener(mutationListener);
  }
  
  private void syncListener(WindowSetMutationEventListener mutationListener) {
    for (Window window: windows) {
      mutationListener.onWindowAdded(this, window);
    }
  }
  
  private class WindowCleanupListener implements WindowMutationEventListener {
    
    @Override
    public void onClose(Window window) {
      windows.remove(window);
    }
    
  }

}
