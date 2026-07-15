package net.buildabrowser.babbrowser.renderer.event;

import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse.SyncEventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public abstract class AbstractEventForwardingTarget implements EventForwardingTarget {

  private final EventForwardingTarget nextTarget;

  public AbstractEventForwardingTarget(
    EventForwardingTarget nextTarget
  ) {
    this.nextTarget = nextTarget;
  }
  
  @Override
  public EventHandlerResponse forwardEvent(
    RendererMouseEvent event,
    SyncEventHandlerResponse prevResponse
  ) {
    if (nextTarget == null) return prevResponse;
    return nextTarget.forwardEvent(event, prevResponse);
  }

  @Override
  public EventHandlerResponse forwardEvent(
    RendererKeyboardEvent event,
    SyncEventHandlerResponse prevResponse
  ) {
    if (nextTarget == null) return prevResponse;
    return nextTarget.forwardEvent(event, prevResponse);
  }

}
