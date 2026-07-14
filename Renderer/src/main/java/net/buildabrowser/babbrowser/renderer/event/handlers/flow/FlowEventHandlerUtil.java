package net.buildabrowser.babbrowser.renderer.event.handlers.flow;

import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public final class FlowEventHandlerUtil {
  
  private FlowEventHandlerUtil() {}

  public static EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext,
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment<?> parentFragment,
    LayoutFragment fragment,
    float relX, float relY
  ) {
    // TODO: Make sure it is on the same stacking context
    return switch (fragment) {
      case PosRefBoxFragment _1 -> EventHandlerResponse.UNHANDLED;
      case ManagedBoxFragment<?> managedFragment -> handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, managedFragment, relX, relY);
      case UnmanagedBoxFragment<?> unmanagedFragment -> handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, unmanagedFragment, relX, relY);
      case LineBoxFragment lineBoxFragment -> handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, lineBoxFragment, relX, relY);
      default -> throw new UnsupportedOperationException();
    };
  }

  private static EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment<?> parentFragment,
    UnmanagedBoxFragment<?> fragment,
    float relX, float relY
  ) {
    if (
      parentFragment.box().stackingContext() != null // TODO: Why is this sometimes null?
      && !parentFragment.box().stackingContext().equals(fragment.box().stackingContext())
    ) return EventHandlerResponse.UNHANDLED;

    return fragment.withEventHandler((eh, f) -> eh.handleMouseEvent(
      eventContext, mouseEvent, f, relX, relY));
  }

  private static EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment<?> parentFragment,
    ManagedBoxFragment<?> fragment,
    float relX, float relY
  ) {
    // TODO: Why is the stacking context sometimes null?
    if (
      parentFragment.box().stackingContext() != null
      && !parentFragment.box().stackingContext().equals(fragment.box().stackingContext())
    ) return EventHandlerResponse.UNHANDLED;

    return handleManagedInnerMouseEvent(
      eventContext, mouseEvent, fragment, fragment.fragments(),
      relX, relY);
  }

  private static EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment<?> parentFragment,
    LineBoxFragment fragment,
    float relX, float relY
  ) {
    return handleManagedInnerMouseEvent(
      eventContext, mouseEvent, parentFragment, fragment.fragments(),
      relX, relY);
  }

  private static EventHandlerResponse handleManagedInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment<?> parentFragment,
    LayoutFragment nextFragment,
    float relX, float relY
  ) {
    LayoutFragment selectedFragment = null;
    // Relies on items not overlapping (relative is handled by stacking contexts)
    while (nextFragment != null) {
      LayoutFragment currentFragment = nextFragment;
      nextFragment = nextFragment.next();

      if (
        nextFragment instanceof BoxFragment boxFragment
        && boxFragment.box().stackingContext() != null
        && !boxFragment.box().stackingContext().equals(parentFragment.box().stackingContext())
      ) continue;

      if (EventUtil.aabb(currentFragment, relX, relY)) {
        selectedFragment = currentFragment;
      }
    }

    EventHandlerResponse childHandledEvent = EventHandlerResponse.UNHANDLED;
    if (selectedFragment instanceof TextFragment textFragment) {
      childHandledEvent = EventUtil.forwardElementEvent(
        eventContext, mouseEvent, parentFragment, textFragment, relX, relY);
    } else if (selectedFragment != null) {
      childHandledEvent = handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, selectedFragment, relX, relY);
    }

    if (childHandledEvent.isUnhandled()) {
      return EventUtil.forwardElementEvent(
        eventContext, mouseEvent, parentFragment, relX, relY);
    }

    return childHandledEvent;
  }

}
