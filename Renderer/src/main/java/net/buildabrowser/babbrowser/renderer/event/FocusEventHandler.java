package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;

public interface FocusEventHandler<T extends BoxContent> {
  
  default EventHandlerResponse handleKeyboardEvent(
    EventContext eventContext,
    T content,
    RendererKeyboardEvent event
  ) {
    return EventHandlerResponse.UNHANDLED;
  }

}
