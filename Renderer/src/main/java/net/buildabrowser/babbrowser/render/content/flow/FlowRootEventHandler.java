package net.buildabrowser.babbrowser.render.content.flow;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public class FlowRootEventHandler implements EventHandler {

  @Override
  public EventHandlerResponse handleMouseEvent(RendererMouseEvent mouseEvent, BoxFragment fragment, float relX, float relY) {
    float contentRelX = relX - fragment.contentX() + fragment.borderX();
    float contentRelY = relY - fragment.contentY() + fragment.borderY();

    FlowRootContent content = (FlowRootContent) fragment.box().content();
    ManagedBoxFragment rootFragment = content.rootFragment();
    if (rootFragment == null) rootFragment = (ManagedBoxFragment) fragment;

    List<BoxFragment> allFloats = content.floatTracker().allFloats();
    ListIterator<BoxFragment> floatIt = allFloats.listIterator(allFloats.size());
    while (floatIt.hasPrevious()) {
      UnmanagedBoxFragment floatFragment = (UnmanagedBoxFragment) floatIt.previous();
      if (EventUtil.aabbZeroAdjusted(floatFragment, contentRelX, contentRelY)) {
        EventHandlerResponse eventHandled = handleInnerMouseEvent(
          mouseEvent, rootFragment, floatFragment, contentRelX, contentRelY);
      
        if (!eventHandled.isUnhandled()) return eventHandled;
      }
    }

    return handleInnerMouseEvent(mouseEvent, rootFragment, rootFragment, relX, relY);
  }

  private EventHandlerResponse handleInnerMouseEvent(
    RendererMouseEvent mouseEvent, ManagedBoxFragment parentFragment, LayoutFragment fragment,
    float relX, float relY
  ) {
    float contentRelX = relX - fragment.contentX() + fragment.borderX();
    float contentRelY = relY - fragment.contentY() + fragment.borderY();

    // TODO: Make sure it is on the same stacking context
    return switch (fragment) {
      case PosRefBoxFragment _ -> EventHandlerResponse.UNHANDLED;
      case ManagedBoxFragment managedFragment -> handleInnerMouseEvent(
        mouseEvent, parentFragment, managedFragment, relX, relY, contentRelX, contentRelY);
      case UnmanagedBoxFragment unmanagedFragment -> handleInnerMouseEvent(
        mouseEvent, parentFragment, unmanagedFragment, contentRelX, contentRelY);
      case LineBoxFragment lineBoxFragment -> handleInnerMouseEvent(
        mouseEvent, parentFragment, lineBoxFragment, relX, relY, contentRelX, contentRelY);
      default -> throw new UnsupportedOperationException();
    };
  }

  private EventHandlerResponse handleInnerMouseEvent(
    RendererMouseEvent mouseEvent, ManagedBoxFragment parentFragment, UnmanagedBoxFragment fragment,
    float contentRelX, float contentRelY
  ) {
    if (
      parentFragment.box().stackingContext() != null // TODO: Why is this sometimes null?
      && !parentFragment.box().stackingContext().equals(fragment.box().stackingContext())
    ) return EventHandlerResponse.UNHANDLED;

    return fragment.box().content().eventHandler().handleMouseEvent(
      mouseEvent, fragment,
      contentRelX - fragment.borderX(),
      contentRelY - fragment.borderY());
  }

  private EventHandlerResponse handleInnerMouseEvent(
    RendererMouseEvent mouseEvent, ManagedBoxFragment parentFragment, ManagedBoxFragment fragment,
    float relX, float relY,
    float contentRelX, float contentRelY
  ) {
    // TODO: Why is the stacking context sometimes null?
    if (
      parentFragment.box().stackingContext() != null
      && !parentFragment.box().stackingContext().equals(fragment.box().stackingContext())
    ) return EventHandlerResponse.UNHANDLED;

    return handleManagedInnerMouseEvent(
      mouseEvent, fragment, fragment.fragments(),
      relX, relY, contentRelX, contentRelY);
  }

  private EventHandlerResponse handleInnerMouseEvent(
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
    LineBoxFragment fragment,
    float relX, float relY,
    float contentRelX, float contentRelY
  ) {
    return handleManagedInnerMouseEvent(
      mouseEvent, parentFragment, fragment.fragments(),
      relX, relY, contentRelX, contentRelY);
  }

  private EventHandlerResponse handleManagedInnerMouseEvent(
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment parentFragment,
    LayoutFragment nextFragment,
    float relX, float relY,
    float contentRelX, float contentRelY
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

      float boxRelX = contentRelX - currentFragment.borderX();
      float boxRelY = contentRelY - currentFragment.borderY();
      if (EventUtil.aabbZeroAdjusted(currentFragment, boxRelX, boxRelY)) {
        selectedFragment = currentFragment;
      }
    }

    EventHandlerResponse childHandledEvent = EventHandlerResponse.UNHANDLED;
    if (selectedFragment instanceof TextFragment textFragment) {
      childHandledEvent = EventUtil.forwardElementEvent(mouseEvent, parentFragment, textFragment, relX, relY);
    } else if (selectedFragment != null) {
      float boxRelX = contentRelX - selectedFragment.borderX();
      float boxRelY = contentRelY - selectedFragment.borderY();
      childHandledEvent = handleInnerMouseEvent(
        mouseEvent, parentFragment, selectedFragment, boxRelX, boxRelY);
    }

    if (childHandledEvent.isUnhandled()) {
      return EventUtil.forwardElementEvent(mouseEvent, parentFragment, relX, relY);
    }

    return childHandledEvent;
  }
  
}
