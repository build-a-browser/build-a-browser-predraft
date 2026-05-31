package net.buildabrowser.babbrowser.renderer.event;

import java.util.Set;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.imp.EventContextImp;

public interface EventContext {
  
  void registerEventObserver(ElementBox observerBox);

  void deregisterEventObserver(ElementBox observerBox);

  Set<ElementBox> eventObservers();

  static EventContext create() {
    return new EventContextImp();
  }

}
