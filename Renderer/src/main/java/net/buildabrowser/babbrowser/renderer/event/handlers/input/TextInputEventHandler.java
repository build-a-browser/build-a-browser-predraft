package net.buildabrowser.babbrowser.renderer.event.handlers.input;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.input.TextInputFragment;

public class TextInputEventHandler implements EventHandler<TextInputFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    TextInputFragment fragment, float relX, float relY
  ) {
    return EventHandlerResponse.UNHANDLED;
  }
  
}
