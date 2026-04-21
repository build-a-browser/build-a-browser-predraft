package net.buildabrowser.babbrowser.render.composite.imp.scroll;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent.MouseEventType;

public class ScrollEventHandler implements EventHandler {

  private ScrollBoxFragment scrollBox;  

  public ScrollEventHandler(ScrollBoxFragment scrollBox) {
    this.scrollBox = scrollBox;
  }

  @Override
  public EventHandlerResponse handleMouseEvent(RendererMouseEvent mouseEvent, BoxFragment fragment, float relX, float relY) {
    UnmanagedBoxFragment innerFragment = scrollBox.innerFragment();
    EventHandlerResponse innerMouseEventResponse = innerFragment.eventHandler().handleMouseEvent(
      mouseEvent, innerFragment, relX + scrollBox.scrollX(), relY + scrollBox.scrollY());
    if (innerMouseEventResponse.equals(EventHandlerResponse.HANDLED)) {
      return innerMouseEventResponse;
    }

    if (
      mouseEvent.event().equals(MouseEventType.SCROLL)
      && (
        (scrollBox.hasHorizontalScroll() && mouseEvent.scrollX() != 0)
        || (scrollBox.hasVerticalScroll() && mouseEvent.scrollY() != 0))
    ) {
      scrollBox.scroll(mouseEvent.scrollX(), mouseEvent.scrollY());
      return EventHandlerResponse.HANDLED;
    }

    return innerMouseEventResponse;
  }

}
