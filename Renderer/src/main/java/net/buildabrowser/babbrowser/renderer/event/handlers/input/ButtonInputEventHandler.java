package net.buildabrowser.babbrowser.renderer.event.handlers.input;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.input.ButtonInputFragment;

public class ButtonInputEventHandler implements EventHandler<ButtonInputFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    ButtonInputFragment fragment, float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    UnmanagedBoxFragment<?> innerFragment = fragment.innerFragment();
    if (EventUtil.aabb(innerFragment, relX, relY)) {
      float relX_ = relX, relY_ = relY;
      EventHandlerResponse innerResponse = innerFragment.withEventHandler(
        (eh, f) -> eh.handleMouseEvent(eventContext, mouseEvent, f, relX_, relY_));
      if (!innerResponse.equals(EventHandlerResponse.UNHANDLED)) {
        return innerResponse;
      }
    }

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }
  
}
