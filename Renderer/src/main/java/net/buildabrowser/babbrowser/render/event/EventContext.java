package net.buildabrowser.babbrowser.render.event;

import java.util.Set;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.event.imp.EventContextImp;

public interface EventContext {
  
  void registerEventObserver(ElementBox observerBox);

  void deregisterEventObserver(ElementBox observerBox);

  Set<ElementBox> eventObservers();

  static EventContext create() {
    return new EventContextImp();
  }

}
