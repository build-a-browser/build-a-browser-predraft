package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.event.EventContext;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public class TableEventHandler implements EventHandler {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    BoxFragment fragment, float relX, float relY
  ) {
    return EventHandlerResponse.UNHANDLED; // TODO: Implement
  }
  
}
