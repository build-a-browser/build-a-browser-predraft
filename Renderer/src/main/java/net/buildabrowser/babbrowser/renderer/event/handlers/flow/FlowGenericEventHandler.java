package net.buildabrowser.babbrowser.renderer.event.handlers.flow;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;

public class FlowGenericEventHandler<T extends ManagedBoxFragment<T>> implements EventHandler<T> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    T fragment,
    float relX, float relY
  ) {
    return FlowEventHandlerUtil.handleInnerMouseEvent(
      eventContext, mouseEvent, fragment, fragment, relX, relY);
  }
  
}
