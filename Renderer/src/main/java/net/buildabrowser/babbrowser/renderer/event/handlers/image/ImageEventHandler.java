package net.buildabrowser.babbrowser.renderer.event.handlers.image;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.image.ImageBoxFragment;

public class ImageEventHandler implements EventHandler<ImageBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    ImageBoxFragment fragment, float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }
  
}
