package net.buildabrowser.babbrowser.renderer.event.handlers;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public class GenericEventHandler<T extends BoxFragment<T>> implements EventHandler<T> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext,
    RendererMouseEvent mouseEvent,
    T fragment,
    float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    return EventUtil.forwardElementEvent(
      eventContext, mouseEvent, fragment, relX, relY);
  }
  
}
