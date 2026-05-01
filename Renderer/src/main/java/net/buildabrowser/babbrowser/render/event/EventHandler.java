package net.buildabrowser.babbrowser.render.event;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

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
