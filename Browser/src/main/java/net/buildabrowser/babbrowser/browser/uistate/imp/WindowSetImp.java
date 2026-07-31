package net.buildabrowser.babbrowser.browser.uistate.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowSetMutationEventListener;
import net.buildabrowser.babbrowser.renderer.uistate.event.BrowserEventDispatcher;

public class WindowSetImp implements WindowSet {
  
  private final BrowserInstance browserInstance;
  
  private final List<Window> windows = new ArrayList<>();
  private final BrowserEventDispatcher<WindowSetMutationEventListener> mutationEventDispatcher = BrowserEventDispatcher.create();

  private final Map<UUID, WeakReference<Window>> tabWindowMappings = new WeakHashMap<>();

  public WindowSetImp(BrowserInstance browserInstance) {
    this.browserInstance = browserInstance;
  }
  
  @Override
  public void close() {
    for (Window window: List.copyOf(windows)) {
      window.close();
    }
    mutationEventDispatcher.fire(listener -> listener.onClose(this));
  }

  @Override
  public void open(URI url) {
    windows.get(0).openTab().navigate(url);
  }

  @Override
  public Tab openTabAfter(UUID uuid) {
    WeakReference<Window> maybeWindow = tabWindowMappings.get(uuid);
    Window window = maybeWindow == null ? null : maybeWindow.get();
    if (window != null) {
      return window.openAfterTab(uuid);
    } else if (windows.size() > 0) {
      // TODO: These fallbacks might result in tab from a private window being
      // opened in a normal window
      return getLast(windows).openAfterTab(uuid);
    } else {
      return openWindow(new WindowOptions(false)).openTab();
    }
  }

  @Override
  public Window[] getWindows() {
    return windows.toArray(new Window[windows.size()]);
  }

  @Override
  public Window openWindow(WindowOptions options) {
    Window window = Window.create(browserInstance, this, options);
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

  @Override
  public void addTabReference(Window window, UUID tabId) {
    tabWindowMappings.put(tabId, new WeakReference<>(window));
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
