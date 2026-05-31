package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public interface EventHandler {
  
  EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    BoxFragment fragment, float relX, float relY
  );

  default void observeMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    BoxFragment fragment, float relX, float relY, boolean preventedDefault
  ) {}

  static enum EventHandlerResponse {
    UNHANDLED, HANDLED, PERFORM_DEFAULT;

    public boolean isUnhandled() {
      return this.equals(UNHANDLED);
    }
  }

}
