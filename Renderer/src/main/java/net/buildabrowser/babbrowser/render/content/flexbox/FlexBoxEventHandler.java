package net.buildabrowser.babbrowser.render.content.flexbox;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public class FlexBoxEventHandler implements EventHandler {

  @Override
  public EventHandlerResponse handleMouseEvent(RendererMouseEvent mouseEvent, BoxFragment fragment, float relX, float relY) {
    float contentRelX = relX - fragment.contentX() + fragment.borderX();
    float contentRelY = relY - fragment.contentY() + fragment.borderY();

    FlexBoxContent content = (FlexBoxContent) fragment.box().content();
    UnmanagedBoxFragment nextFragment = content.fragments();

    EventHandlerResponse childHandledEvent = handleChildMouseEvent(mouseEvent, fragment, nextFragment, relX, relY, contentRelX, contentRelY);
    if (!childHandledEvent.isUnhandled()) return childHandledEvent;

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }

  private EventHandlerResponse handleChildMouseEvent(
    RendererMouseEvent mouseEvent,
    BoxFragment parentFragment,
    UnmanagedBoxFragment nextFragment,
    float relX, float relY,
    float contentRelX, float contentRelY
  ) {
    UnmanagedBoxFragment selectedFragment = null;
    // Relies on items not overlapping (relative is handled by stacking contexts)
    while (nextFragment != null) {
      UnmanagedBoxFragment currentFragment = nextFragment;
      nextFragment = (UnmanagedBoxFragment) nextFragment.next();

      if (
        currentFragment.box().stackingContext() != null // TODO: Why is this sometimes null?
        && !currentFragment.box().stackingContext().equals(parentFragment.box().stackingContext())
      ) continue;

      float boxRelX = contentRelX - currentFragment.borderX();
      float boxRelY = contentRelY - currentFragment.borderY();
      if (EventUtil.aabbZeroAdjusted(currentFragment, boxRelX, boxRelY)) {
        selectedFragment = currentFragment;
      }
    }

    if (selectedFragment != null) {
      float boxRelX = contentRelX - selectedFragment.borderX();
      float boxRelY = contentRelY - selectedFragment.borderY();

      return selectedFragment.box().content().eventHandler().handleMouseEvent(
        mouseEvent, selectedFragment, boxRelX, boxRelY);
    }

    return EventHandlerResponse.UNHANDLED;
  }
  
}
