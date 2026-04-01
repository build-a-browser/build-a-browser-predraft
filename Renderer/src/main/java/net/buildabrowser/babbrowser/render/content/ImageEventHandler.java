package net.buildabrowser.babbrowser.render.content;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;

public class ImageEventHandler implements EventHandler {

  @Override
  public boolean handleMouseEvent(
    MouseEvent mouseEvent, BoxFragment fragment, float relX, float relY
  ) {
    if (!EventUtil.aabbZeroAdjusted(fragment, relX, relY)) return false;

    relX -= fragment.borderX();
    relY -= fragment.borderY();

    EventUtil.forwardElementEvent(mouseEvent, (BoxFragment) fragment, relX, relY);
    return true;
  }
  
}
