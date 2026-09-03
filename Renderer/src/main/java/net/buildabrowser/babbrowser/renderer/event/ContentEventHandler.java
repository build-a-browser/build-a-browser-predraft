package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;

public interface ContentEventHandler<T extends BoxContent> {
  
  default EventHandlerResponse handleKeyboardEvent(
    EventContext eventContext,
    ElementBox box,
    T content,
    RendererKeyboardEvent event
  ) {
    return EventHandlerResponse.UNHANDLED;
  }

  default EventHandlerResponse handleElementEvent(
    Element target, Event event
  ) {
    return EventHandlerResponse.UNHANDLED;
  }

}
