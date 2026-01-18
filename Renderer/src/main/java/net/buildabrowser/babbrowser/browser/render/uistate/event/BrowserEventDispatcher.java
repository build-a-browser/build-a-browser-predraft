package net.buildabrowser.babbrowser.browser.render.uistate.event;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.render.uistate.event.imp.BrowserEventDispatcherImp;

public interface BrowserEventDispatcher<T extends BrowserEventListener> {

  void addListener(T listener);
  
  void removeListener(T listener);
  
  void fire(Consumer<T> eventFunc);

  static <T extends BrowserEventListener> BrowserEventDispatcher<T> create() {
    return new BrowserEventDispatcherImp<>();
  }
  
}