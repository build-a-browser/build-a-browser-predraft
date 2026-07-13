package net.buildabrowser.babbrowser.renderer.event.handlers.input;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.input.HiddenInputFragment;

public class HiddenInputEventHandler implements EventHandler<HiddenInputFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    HiddenInputFragment fragment, float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }
  
}
