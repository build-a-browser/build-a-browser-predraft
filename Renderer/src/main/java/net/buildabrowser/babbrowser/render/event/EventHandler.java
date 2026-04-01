package net.buildabrowser.babbrowser.render.event;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;

public interface EventHandler {
  
  boolean handleMouseEvent(MouseEvent mouseEvent, BoxFragment fragment, float relX, float relY);

  record MouseEvent(
    float winX, float winY,
    int button, MouseEventType event
  ) {
    public static enum MouseEventType {
      CLICK
    }
  }

}
