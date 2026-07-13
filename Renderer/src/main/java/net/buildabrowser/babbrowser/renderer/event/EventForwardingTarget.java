package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public interface EventForwardingTarget {
  
  default EventHandlerResponse forwardEvent(RendererMouseEvent event) {
    return EventHandlerResponse.UNHANDLED;
  }

  default EventHandlerResponse forwardEvent(RendererKeyboardEvent event) {
    return EventHandlerResponse.UNHANDLED;
  }

}
