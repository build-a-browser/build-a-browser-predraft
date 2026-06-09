package net.buildabrowser.babbrowser.renderer.event.handlers.table;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;

public class TableEventHandler implements EventHandler<TableBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    TableBoxFragment fragment, float relX, float relY
  ) {
    return EventHandlerResponse.UNHANDLED; // TODO: Implement
  }
  
}
