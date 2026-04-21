package net.buildabrowser.babbrowser.render.content.flexbox;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public class FlexBoxEventHandler implements EventHandler {

  @Override
  public EventHandlerResponse handleMouseEvent(RendererMouseEvent mouseEvent, BoxFragment fragment, float relX, float relY) {
    FlexBoxContent content = (FlexBoxContent) fragment.box().content();
    UnmanagedBoxFragment nextFragment = content.fragments();

    EventHandlerResponse childHandledEvent = handleChildMouseEvent(mouseEvent, fragment, nextFragment, relX, relY);
    if (!childHandledEvent.isUnhandled()) return childHandledEvent;

    return EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
  }

  private EventHandlerResponse handleChildMouseEvent(
    RendererMouseEvent mouseEvent,
    BoxFragment parentFragment,
    UnmanagedBoxFragment nextFragment,
    float relX, float relY
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

      if (EventUtil.aabb(currentFragment, relX, relY)) {
        selectedFragment = currentFragment;
      }
    }

    if (selectedFragment != null) {
      return selectedFragment.box().content().eventHandler().handleMouseEvent(
        mouseEvent, selectedFragment, relX, relY);
    }

    return EventHandlerResponse.UNHANDLED;
  }
  
}
