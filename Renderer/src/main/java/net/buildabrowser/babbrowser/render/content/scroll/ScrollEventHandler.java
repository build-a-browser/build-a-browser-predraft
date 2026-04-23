package net.buildabrowser.babbrowser.render.content.scroll;

import static net.buildabrowser.babbrowser.render.content.scroll.ScrollContentPainter.GUTTER_WIDTH;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollMath.ScrollMathResult;
import net.buildabrowser.babbrowser.render.event.EventContext;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent.MouseEventType;

public class ScrollEventHandler implements EventHandler {

  private ScrollBox scrollBox;  

  public ScrollEventHandler(ScrollBox scrollBox) {
    this.scrollBox = scrollBox;
  }

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    BoxFragment fragment, float relX, float relY
  ) {
    ScrollBoxFragment scrollBoxFragment = scrollBox.scrollFragment();
    if (scrollBoxFragment == null) return EventHandlerResponse.UNHANDLED;
    UnmanagedBoxFragment innerFragment = scrollBoxFragment.innerFragment();
    EventHandlerResponse innerMouseEventResponse = innerFragment.eventHandler().handleMouseEvent(
      eventContext, mouseEvent,
      innerFragment, relX + scrollBox.scrollX(), relY + scrollBox.scrollY());
    if (innerMouseEventResponse.equals(EventHandlerResponse.HANDLED)) {
      disableIfScrollRelated(eventContext, mouseEvent);
      return innerMouseEventResponse;
    }

    if (
      mouseEvent.event().equals(MouseEventType.SCROLL)
      && (
        (scrollBoxFragment.hasHorizontalScroll() && mouseEvent.scrollX() != 0)
        || (scrollBoxFragment.hasVerticalScroll() && mouseEvent.scrollY() != 0))
    ) {
      scrollBox.scroll(mouseEvent.scrollX(), mouseEvent.scrollY());
      return EventHandlerResponse.HANDLED;
    }

    if (mouseEvent.event().equals(MouseEventType.MOVE)) {
      handleMoveEvent(relX, relY);
    } else if (mouseEvent.event().equals(MouseEventType.DOWN)) {
      handleDownEvent(mouseEvent, relX, relY);
    }

    if (
      scrollBox.verticalScrollState().hovered()
      || scrollBox.horizontalScrollState().hovered()
      || scrollBox.verticalScrollState().active()
      || scrollBox.horizontalScrollState().active()
    ) {
      eventContext.registerEventObserver(scrollBox);
    }

    return innerMouseEventResponse;
  }

  @Override
  public void observeMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    BoxFragment fragment, float relX, float relY, boolean preventDefault
  ) {
    if (preventDefault) {
      disableIfScrollRelated(eventContext, mouseEvent);
      return;
    }

    if (mouseEvent.event().equals(MouseEventType.MOVE)) {
      handleMoveEvent(relX, relY);
      handleDragEvent(mouseEvent);
    } else if (mouseEvent.event().equals(MouseEventType.UP)) {
      handleUpEvent(eventContext, relX, relY);
    }
  }

  private void handleMoveEvent(float relX, float relY) {
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    boolean wasVerticalHovered = verticalScrollState.hovered();
    boolean wasHorizontalHovered = horizontalScrollState.hovered();
    if (hitsVertical(relX, relY)) {
      verticalScrollState.setHovered(true);
      horizontalScrollState.setHovered(false);
    } else if (hitsHorizontal(relX, relY)) {
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
      scrollBox.element().invalidate(InvalidationLevel.PAINT);
    }
  }

  private void handleDownEvent(RendererMouseEvent event, float relX, float relY) {
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    boolean wasVerticalActive = verticalScrollState.active();
    boolean wasHorizontalActive = horizontalScrollState.active();
    if (hitsVertical(relX, relY) && !wasVerticalActive) {
      verticalScrollState.activate(event.winY(), scrollBox.scrollY());
    } else if (hitsHorizontal(relX, relY) && !wasHorizontalActive) {
      horizontalScrollState.activate(event.winX(), scrollBox.scrollX());
    }

    if (
      verticalScrollState.active() != wasVerticalActive
      || horizontalScrollState.active() != wasHorizontalActive
    ) {
      scrollBox.element().invalidate(InvalidationLevel.PAINT);
    }
  }

  private void handleUpEvent(EventContext eventContext, float relX, float relY) {
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    eventContext.deregisterEventObserver(scrollBox);
    if (
      verticalScrollState.active()
      || horizontalScrollState.active()
    ) {
      scrollBox.element().invalidate(InvalidationLevel.PAINT);
    }
    verticalScrollState.deactivate();
    horizontalScrollState.deactivate();

    handleMoveEvent(relX, relY);
  }

  private void handleDragEvent(RendererMouseEvent mouseEvent) {
    ScrollBoxFragment scrollBoxFragment = scrollBox.scrollFragment();

    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    if (verticalScrollState.active()) {
      ScrollMathResult scrollInfo = scrollBoxFragment.verticalScrollInfo();
      float diffY = mouseEvent.winY() - verticalScrollState.winStart();
      float innerHeight = scrollBoxFragment.innerFragment().inkHeight(Measurement.CONTENT);
      float diffScroll = diffY / scrollInfo.trackSize() * innerHeight;
      float newScroll = verticalScrollState.scrollStart() + diffScroll;
      scrollBox.setScrollY(newScroll);

      scrollBox.element().invalidate(InvalidationLevel.PAINT);
    }

    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();
    if (horizontalScrollState.active()) {
      ScrollMathResult scrollInfo = scrollBoxFragment.horizontalScrollInfo();
      float diffX = mouseEvent.winX() - horizontalScrollState.winStart();
      float innerWidth = scrollBoxFragment.innerFragment().inkWidth(Measurement.CONTENT);
      float diffScroll = diffX / scrollInfo.trackSize() * innerWidth;
      float newScroll = horizontalScrollState.scrollStart() + diffScroll;
      scrollBox.setScrollX(newScroll);

      scrollBox.element().invalidate(InvalidationLevel.PAINT);
    }
  }

  private void disableIfScrollRelated(EventContext eventContext, RendererMouseEvent mouseEvent) {
    if (
      mouseEvent.event().equals(MouseEventType.MOVE)
    ) {
      disableScrollBars(eventContext);
    }
  }

  private void disableScrollBars(EventContext eventContext) {
    ScrollBarState verticalScrollState = scrollBox.verticalScrollState();
    ScrollBarState horizontalScrollState = scrollBox.horizontalScrollState();

    if (
      horizontalScrollState.hovered()
      || verticalScrollState.hovered()
    ) {
      scrollBox.element().invalidate(InvalidationLevel.PAINT);
    }

    horizontalScrollState.setHovered(false);
    verticalScrollState.setHovered(false);
    horizontalScrollState.deactivate();
    verticalScrollState.deactivate();

    eventContext.deregisterEventObserver(scrollBox);
  }

  private boolean hitsHorizontal(float relX, float relY) {
    ScrollBoxFragment scrollBoxFragment = scrollBox.scrollFragment();
    ScrollMathResult horizontalScrollInfo = scrollBoxFragment.horizontalScrollInfo();
    return scrollBoxFragment.hasHorizontalScroll()
      && relX >= horizontalScrollInfo.trackX() + horizontalScrollInfo.scrollerPos()
      && relX < horizontalScrollInfo.trackX() + horizontalScrollInfo.scrollerPos() + horizontalScrollInfo.scrollerSize()
      && relY >= horizontalScrollInfo.trackY()
      && relY < horizontalScrollInfo.trackY() + GUTTER_WIDTH;
  }

  private boolean hitsVertical(float relX, float relY) {
    ScrollBoxFragment scrollBoxFragment = scrollBox.scrollFragment();
    ScrollMathResult verticalScrollInfo = scrollBoxFragment.verticalScrollInfo();
    return scrollBoxFragment.hasVerticalScroll()
      && relX >= verticalScrollInfo.trackX()
      && relX < verticalScrollInfo.trackX() + GUTTER_WIDTH
      && relY >= verticalScrollInfo.trackY() + verticalScrollInfo.scrollerPos()
      && relY < verticalScrollInfo.trackY() + verticalScrollInfo.scrollerPos() + verticalScrollInfo.scrollerSize();
  }

}
