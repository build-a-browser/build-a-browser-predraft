package net.buildabrowser.babbrowser.render.content.flexbox;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;

public class FlexBoxEventHandler implements EventHandler {

  @Override
  public boolean handleMouseEvent(MouseEvent mouseEvent, BoxFragment fragment, float relX, float relY) {
    if (!EventUtil.aabbZeroAdjusted(fragment, relX, relY)) return false;
    
    float contentRelX = relX - fragment.contentX() + fragment.borderX();
    float contentRelY = relY - fragment.contentY() + fragment.borderY();

    FlexBoxContent content = (FlexBoxContent) fragment.box().content();
    UnmanagedBoxFragment nextFragment = content.fragments();

    boolean childHandledEvent = handleChildMouseEvent(mouseEvent, fragment, nextFragment, relX, relY, contentRelX, contentRelY);
    if (childHandledEvent) return true;

    EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
    return true;
  }

  private boolean handleChildMouseEvent(
    MouseEvent mouseEvent,
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
        !currentFragment.box().stackingContext().equals(parentFragment.box().stackingContext())
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

    return false;
  }
  
}
