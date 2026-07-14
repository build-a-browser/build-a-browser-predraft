package net.buildabrowser.babbrowser.renderer.event.imp;

import java.util.Set;
import java.util.WeakHashMap;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventContext;

public class EventContextImp implements EventContext {

  // Automatically removes elements for us.
  // A proper Set<WeakReference<ElementBox>> would retain internal set nodes.
  private WeakHashMap<ElementBox, Boolean> observerBoxes = new WeakHashMap<>();

  private boolean preventDefault = false;

  @Override
  public void registerEventInterceptor(ElementBox observerBox) {
    observerBoxes.put(observerBox, true);
  }

  @Override
  public void deregisterEventObserver(ElementBox observerBox) {
    observerBoxes.remove(observerBox);
  }

  @Override
  public Set<ElementBox> eventObservers() {
    return observerBoxes.keySet();
  }

  @Override
  public void setPreventDefault(boolean preventDefault) {
    this.preventDefault = preventDefault;
  }

  @Override
  public boolean isPreventDefault() {
    return this.preventDefault;
  }
  
}
