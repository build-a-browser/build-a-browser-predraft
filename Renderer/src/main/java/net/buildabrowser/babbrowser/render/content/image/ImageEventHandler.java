package net.buildabrowser.babbrowser.render.content.image;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.event.EventContext;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public class ImageEventHandler implements EventHandler {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    BoxFragment fragment, float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    return EventUtil.forwardElementEvent(mouseEvent, (BoxFragment) fragment, relX, relY);
  }
  
}
