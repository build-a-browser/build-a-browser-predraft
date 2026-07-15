package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse.SyncEventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public interface EventForwardingTarget {
  
  default EventHandlerResponse forwardEvent(
    RendererMouseEvent event,
    SyncEventHandlerResponse prevResponse
  ) {
    return prevResponse;
  }

  default EventHandlerResponse forwardEvent(
    RendererKeyboardEvent event,
    SyncEventHandlerResponse prevResponse
  ) {
    return prevResponse;
  }

}
