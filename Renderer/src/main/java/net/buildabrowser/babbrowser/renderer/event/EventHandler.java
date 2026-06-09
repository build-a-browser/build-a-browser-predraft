package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;

public interface EventHandler<T extends BoxFragment<T>> {
  
  EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    T fragment, float relX, float relY
  );

  default void observeMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    T fragment, float relX, float relY, boolean preventedDefault
  ) {}

  static enum EventHandlerResponse {
    UNHANDLED, HANDLED, PERFORM_DEFAULT;

    public boolean isUnhandled() {
      return this.equals(UNHANDLED);
    }
  }

}
