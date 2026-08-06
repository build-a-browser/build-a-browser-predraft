package net.buildabrowser.babbrowser.renderer.event.handlers.scroll;

import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.GUTTER_WIDTH;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBarState;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollMath.ScrollMathResult;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent.MouseEventType;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;

public class ScrollEventHandler implements EventHandler<ScrollBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    ScrollBoxFragment scrollBoxFragment, float relX, float relY
  ) {
    if (scrollBoxFragment == null) return EventHandlerResponse.UNHANDLED;
    ScrollBox scrollBox = scrollBoxFragment.box();
    UnmanagedBoxFragment<?> innerFragment = scrollBoxFragment.innerFragment();
    EventHandlerResponse innerMouseEventResponse = EventUtil.aabb(innerFragment, relX, relY) ?
      innerFragment.withEventHandler((eh, f) -> eh.handleMouseEvent(
        eventContext, mouseEvent, f,
        relX + scrollBoxFragment.scrollX(),
        relY + scrollBoxFragment.scrollY())) :
      EventHandlerResponse.UNHANDLED;
    if (innerMouseEventResponse.equals(EventHandlerResponse.HANDLED)) {
      disableIfScrollRelated(eventContext, scrollBoxFragment, mouseEvent);
      return innerMouseEventResponse;
    }

    if (
      mouseEvent.event().equals(MouseEventType.SCROLL)
      && (
        (scrollBoxFragment.hasHorizontalScroll() && mouseEvent.scrollX() != 0)
        || (scrollBoxFragment.hasVerticalScroll() && mouseEvent.scrollY() != 0))
    ) {
      scrollBoxFragment.scroll(mouseEvent.scrollX(), mouseEvent.scrollY());
      immediateRepaint(scrollBox);
      return EventHandlerResponse.HANDLED;
    }

    if (mouseEvent.event().equals(MouseEventType.MOVE)) {
      handleMoveEvent(scrollBoxFragment, relX, relY);
    } else if (mouseEvent.event().equals(MouseEventType.DOWN)) {
      handleDownEvent(mouseEvent, scrollBoxFragment, relX, relY);
    }

    if (
      scrollBox.verticalScrollState().hovered()
      || scrollBox.horizontalScrollState().hovered()
      || scrollBox.verticalScrollState().active()
      || scrollBox.horizontalScrollState().active()
    ) {
      eventContext.registerEventInterceptor(scrollBox);
    }

    return innerMouseEventResponse;
  }

  @Override
  public boolean interceptMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    ScrollBoxFragment scrollBoxFragment,
    float relX, float relY
  ) {
    if (mouseEvent.event().equals(MouseEventType.MOVE)) {
      handleMoveEvent(scrollBoxFragment, relX, relY);
      handleDragEvent(mouseEvent, scrollBoxFragment);
      return true;
    } else if (mouseEvent.event().equals(MouseEventType.UP)) {
      handleUpEvent(eventContext, scrollBoxFragment, relX, relY);
      return true;
    }

    return false;
  }

  private void handleMoveEvent(
    ScrollBoxFragment scrollBoxFragment,
    float relX, float relY
  ) {
    ScrollBox scrollBox = scrollBoxFragment.box();
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    boolean wasVerticalHovered = verticalScrollState.hovered();
    boolean wasHorizontalHovered = horizontalScrollState.hovered();
    if (hitsVertical(scrollBoxFragment, relX, relY)) {
      verticalScrollState.setHovered(true);
      horizontalScrollState.setHovered(false);
    } else if (hitsHorizontal(scrollBoxFragment, relX, relY)) {
      horizontalScrollState.setHovered(true);
      verticalScrollState.setHovered(false);
    } else if (
      horizontalScrollState.hovered()
      || verticalScrollState.hovered()
    ) {
      horizontalScrollState.setHovered(false);
      verticalScrollState.setHovered(false);
    }

    if (
      verticalScrollState.hovered() != wasVerticalHovered
      || horizontalScrollState.hovered() != wasHorizontalHovered
    ) {
      immediateRepaint(scrollBox);
    }
  }

  private void handleDownEvent(
    RendererMouseEvent event,
    ScrollBoxFragment scrollBoxFragment,
    float relX, float relY
  ) {
    ScrollBox scrollBox = scrollBoxFragment.box();
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    boolean wasVerticalActive = verticalScrollState.active();
    boolean wasHorizontalActive = horizontalScrollState.active();
    if (!wasVerticalActive && hitsVertical(scrollBoxFragment, relX, relY)) {
      verticalScrollState.activate(event.winY(), scrollBoxFragment.scrollY());
    } else if (!wasHorizontalActive &&hitsHorizontal(scrollBoxFragment, relX, relY)) {
      horizontalScrollState.activate(event.winX(), scrollBoxFragment.scrollX());
    }

    if (
      verticalScrollState.active() != wasVerticalActive
      || horizontalScrollState.active() != wasHorizontalActive
    ) {
      immediateRepaint(scrollBox);
    }
  }

  private void handleUpEvent(
    EventContext eventContext,
    ScrollBoxFragment scrollBoxFragment,
    float relX, float relY
  ) {
    ScrollBox scrollBox = scrollBoxFragment.box();
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    eventContext.deregisterEventObserver(scrollBox);
    if (
      verticalScrollState.active()
      || horizontalScrollState.active()
    ) {
      immediateRepaint(scrollBox);
    }
    verticalScrollState.deactivate();
    horizontalScrollState.deactivate();

    handleMoveEvent(scrollBoxFragment, relX, relY);
  }

  private void handleDragEvent(
    RendererMouseEvent mouseEvent,
    ScrollBoxFragment scrollBoxFragment
  ) {
    ScrollBox scrollBox = scrollBoxFragment.box();
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    if (verticalScrollState.active()) {
      ScrollMathResult scrollInfo = scrollBoxFragment.verticalScrollInfo();
      float diffY = mouseEvent.winY() - verticalScrollState.winStart();
      float innerHeight = scrollBoxFragment.innerFragment().inkHeight(Measurement.PADDING);
      float diffScroll = diffY / scrollInfo.trackSize() * innerHeight;
      float newScroll = verticalScrollState.scrollStart() + diffScroll;
      scrollBoxFragment.setScrollY(newScroll);

      immediateRepaint(scrollBox);
    }

    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();
    if (horizontalScrollState.active()) {
      ScrollMathResult scrollInfo = scrollBoxFragment.horizontalScrollInfo();
      float diffX = mouseEvent.winX() - horizontalScrollState.winStart();
      float innerWidth = scrollBoxFragment.innerFragment().inkWidth(Measurement.PADDING);
      float diffScroll = diffX / scrollInfo.trackSize() * innerWidth;
      float newScroll = horizontalScrollState.scrollStart() + diffScroll;
      scrollBoxFragment.setScrollX(newScroll);

      immediateRepaint(scrollBox);
    }
  }

  private void disableIfScrollRelated(
    EventContext eventContext,
    ScrollBoxFragment scrollBoxFragment,
    RendererMouseEvent mouseEvent
  ) {
    if (
      mouseEvent.event().equals(MouseEventType.MOVE)
    ) {
      disableScrollBars(eventContext, scrollBoxFragment);
    }
  }

  private void disableScrollBars(
    EventContext eventContext,
    ScrollBoxFragment scrollBoxFragment
  ) {
    ScrollBox scrollBox = scrollBoxFragment.box();
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    if (
      horizontalScrollState.hovered()
      || verticalScrollState.hovered()
    ) {
      immediateRepaint(scrollBox);
    }

    horizontalScrollState.setHovered(false);
    verticalScrollState.setHovered(false);
    horizontalScrollState.deactivate();
    verticalScrollState.deactivate();

    eventContext.deregisterEventObserver(scrollBox);
  }

  private boolean hitsHorizontal(
    ScrollBoxFragment scrollBoxFragment,
    float relX, float relY
  ) {
    ScrollMathResult horizontalScrollInfo = scrollBoxFragment.horizontalScrollInfo();
    return scrollBoxFragment.hasHorizontalScroll()
      && relX >= horizontalScrollInfo.trackX() + horizontalScrollInfo.scrollerPos()
      && relX < horizontalScrollInfo.trackX() + horizontalScrollInfo.scrollerPos() + horizontalScrollInfo.scrollerSize()
      && relY >= horizontalScrollInfo.trackY()
      && relY < horizontalScrollInfo.trackY() + GUTTER_WIDTH;
  }

  private boolean hitsVertical(
    ScrollBoxFragment scrollBoxFragment,
    float relX, float relY
  ) {
    ScrollMathResult verticalScrollInfo = scrollBoxFragment.verticalScrollInfo();
    return scrollBoxFragment.hasVerticalScroll()
      && relX >= verticalScrollInfo.trackX()
      && relX < verticalScrollInfo.trackX() + GUTTER_WIDTH
      && relY >= verticalScrollInfo.trackY() + verticalScrollInfo.scrollerPos()
      && relY < verticalScrollInfo.trackY() + verticalScrollInfo.scrollerPos() + verticalScrollInfo.scrollerSize();
  }

  private void immediateRepaint(
    ScrollBox scrollBox
  ) {
    HTMLDocument document = (HTMLDocument) scrollBox.element().nodeDocument();
    Navigable navigable = document.nodeNavigable();
    navigable.uaNavigableOptions().requestRepaint();
    EventLoop.queueGlobalTask(TaskSource.USER_INTERACTION, navigable.activeWindow(),
      () -> scrollBox.context().invalidate(InvalidationLevel.PAINT));
  }

}
