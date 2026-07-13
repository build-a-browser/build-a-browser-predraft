package net.buildabrowser.babbrowser.renderer.event.handlers.flow;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;

public class FlowRootEventHandler implements EventHandler<FlowRootBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    FlowRootBoxFragment fragment,
    float relX, float relY
  ) {
    ManagedBoxFragment<?> rootFragment = fragment.rootFragment();

    List<BoxFragment<?>> allFloats = fragment.floats();
    ListIterator<BoxFragment<?>> floatIt = allFloats.listIterator(allFloats.size());
    while (floatIt.hasPrevious()) {
      UnmanagedBoxFragment<?> floatFragment = (UnmanagedBoxFragment<?>) floatIt.previous();
      if (EventUtil.aabb(floatFragment, relX, relY)) {
        EventHandlerResponse eventHandled = FlowEventHandlerUtil.handleInnerMouseEvent(
          eventContext, mouseEvent, rootFragment, floatFragment, relX, relY);
      
        if (!eventHandled.isUnhandled()) return eventHandled;
      }
    }

    return FlowEventHandlerUtil.handleInnerMouseEvent(
      eventContext, mouseEvent, rootFragment, rootFragment, relX, relY);
  }
  
}
