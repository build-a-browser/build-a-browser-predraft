package net.buildabrowser.babbrowser.browser.render.uistate.event.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventDispatcher;
import net.buildabrowser.babbrowser.browser.render.uistate.event.BrowserEventListener;

public class BrowserEventDispatcherImp<T extends BrowserEventListener> implements BrowserEventDispatcher<T> {
  
  private List<T> listeners = new ArrayList<>();

  @Override
  public void addListener(T listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(T listener) {
    listeners.remove(listener);
  }

  @Override
  public void fire(Consumer<T> eventFunc) {
    for (T listener: List.copyOf(listeners)) {
      eventFunc.accept(listener);
    }
  }

}