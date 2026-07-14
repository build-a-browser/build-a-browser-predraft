package net.buildabrowser.babbrowser.renderer.event;

import java.util.Set;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.imp.EventContextImp;

public interface EventContext {
  
  void registerEventInterceptor(ElementBox observerBox);

  void deregisterEventObserver(ElementBox observerBox);

  Set<ElementBox> eventObservers();

  // Because I don't feel like routing the interceptor result down the tree

  void setPreventDefault(boolean preventDefault);

  boolean isPreventDefault();

  static EventContext create() {
    return new EventContextImp();
  }

}
