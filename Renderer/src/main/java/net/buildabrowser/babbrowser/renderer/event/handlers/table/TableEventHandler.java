package net.buildabrowser.babbrowser.renderer.event.handlers.table;

import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;

public class TableEventHandler implements EventHandler<TableBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    TableBoxFragment fragment, float relX, float relY
  ) {
    Table table = fragment.table();
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() == null) continue;
          EventHandlerResponse childHandledEvent = handleCellMouseEvent(
            eventContext, mouseEvent,
            fragment, cell.getRelatedFragment(),
            relX, relY);
          if (!childHandledEvent.isUnhandled()) return childHandledEvent;
        }
      }
    }

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }

  private static EventHandlerResponse handleCellMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    TableBoxFragment parentFragment,
    UnmanagedBoxFragment<?> cellFragment,
    float relX, float relY
  ) {
    if (
      parentFragment.box().stackingContext() != null
      && !parentFragment.box().stackingContext().equals(cellFragment.box().stackingContext())
    ) return EventHandlerResponse.UNHANDLED;
    if (
      !EventUtil.aabb(parentFragment, cellFragment, relX, relY)
    ) return EventHandlerResponse.UNHANDLED;

    return cellFragment.withEventHandler((eh, f) -> eh.handleMouseEvent(
      eventContext, mouseEvent, f, relX, relY));
  }
  
}
