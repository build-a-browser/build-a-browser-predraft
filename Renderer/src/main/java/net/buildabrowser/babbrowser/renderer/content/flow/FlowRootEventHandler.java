package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public class FlowRootEventHandler implements EventHandler {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    BoxFragment fragment,
    float relX, float relY
  ) {
    FlowRootContent content = (FlowRootContent) fragment.box().content();
    ManagedBoxFragment rootFragment = content.rootFragment();
    if (rootFragment == null) rootFragment = (ManagedBoxFragment) fragment;

    List<BoxFragment> allFloats = content.floatTracker().allFloats();
    ListIterator<BoxFragment> floatIt = allFloats.listIterator(allFloats.size());
    while (floatIt.hasPrevious()) {
      UnmanagedBoxFragment floatFragment = (UnmanagedBoxFragment) floatIt.previous();
      if (EventUtil.aabb(floatFragment, relX, relY)) {
        EventHandlerResponse eventHandled = handleInnerMouseEvent(
          eventContext, mouseEvent, rootFragment, floatFragment, relX, relY);
      
        if (!eventHandled.isUnhandled()) return eventHandled;
      }
    }

    return handleInnerMouseEvent(
      eventContext, mouseEvent, rootFragment, rootFragment, relX, relY);
  }

  private EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext,
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
    LayoutFragment fragment,
    float relX, float relY
  ) {
    // TODO: Make sure it is on the same stacking context
    return switch (fragment) {
      case PosRefBoxFragment _ -> EventHandlerResponse.UNHANDLED;
      case ManagedBoxFragment managedFragment -> handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, managedFragment, relX, relY);
      case UnmanagedBoxFragment unmanagedFragment -> handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, unmanagedFragment, relX, relY);
      case LineBoxFragment lineBoxFragment -> handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, lineBoxFragment, relX, relY);
      default -> throw new UnsupportedOperationException();
    };
  }

  private EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
    UnmanagedBoxFragment fragment,
    float relX, float relY
  ) {
    if (
      parentFragment.box().stackingContext() != null // TODO: Why is this sometimes null?
      && !parentFragment.box().stackingContext().equals(fragment.box().stackingContext())
    ) return EventHandlerResponse.UNHANDLED;

    return fragment.box().content().eventHandler().handleMouseEvent(
      eventContext, mouseEvent, fragment, relX, relY);
  }

  private EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
    ManagedBoxFragment fragment,
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

  private EventHandlerResponse handleInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
    LineBoxFragment fragment,
    float relX, float relY
  ) {
    return handleManagedInnerMouseEvent(
      eventContext, mouseEvent, parentFragment, fragment.fragments(),
      relX, relY);
  }

  private EventHandlerResponse handleManagedInnerMouseEvent(
    EventContext eventContext, 
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
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

      if (EventUtil.aabb(parentFragment, currentFragment, relX, relY)) {
        selectedFragment = currentFragment;
      }
    }

    EventHandlerResponse childHandledEvent = EventHandlerResponse.UNHANDLED;
    if (selectedFragment instanceof TextFragment textFragment) {
      childHandledEvent = EventUtil.forwardElementEvent(mouseEvent, parentFragment, textFragment, relX, relY);
    } else if (selectedFragment != null) {
      childHandledEvent = handleInnerMouseEvent(
        eventContext, mouseEvent, parentFragment, selectedFragment, relX, relY);
    }

    if (childHandledEvent.isUnhandled()) {
      return EventUtil.forwardElementEvent(mouseEvent, parentFragment, relX, relY);
    }

    return childHandledEvent;
  }
  
}
