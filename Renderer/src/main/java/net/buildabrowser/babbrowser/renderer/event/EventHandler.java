package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;

public interface EventHandler<T extends BoxFragment<T>> {
  
  EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    T fragment, float relX, float relY
  );

  // Returns boolean to prevent default
  default boolean interceptMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    T fragment, float relX, float relY
  ) {
    return false;
  }

}
