package net.buildabrowser.babbrowser.renderer.event.handlers.flexbox;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;

public class FlexBoxEventHandler implements EventHandler<FlexBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    FlexBoxFragment fragment, float relX, float relY
  ) {
    UnmanagedBoxFragment<?> nextFragment = fragment.fragments();

    EventHandlerResponse childHandledEvent = handleChildMouseEvent(
      eventContext, mouseEvent, fragment, nextFragment, relX, relY);
    if (!childHandledEvent.isUnhandled()) return childHandledEvent;

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }

  private EventHandlerResponse handleChildMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    FlexBoxFragment parentFragment,
    UnmanagedBoxFragment<?> nextFragment,
    float relX, float relY
  ) {
    UnmanagedBoxFragment<?> selectedFragment = null;
    // Relies on items not overlapping (relative is handled by stacking contexts)
    while (nextFragment != null) {
      UnmanagedBoxFragment<?> currentFragment = nextFragment;
      nextFragment = (UnmanagedBoxFragment<?>) nextFragment.next();

      if (
        currentFragment.box().stackingContext() != null // TODO: Why is this sometimes null?
        && !currentFragment.box().stackingContext().equals(parentFragment.box().stackingContext())
      ) continue;

      if (EventUtil.aabb(currentFragment, relX, relY)) {
        selectedFragment = currentFragment;
      }
    }

    if (selectedFragment != null) {
      return selectedFragment.withEventHandler((eh, f) -> eh.handleMouseEvent(
        eventContext, mouseEvent, f, relX, relY));
    }

    return EventHandlerResponse.UNHANDLED;
  }
  
}
