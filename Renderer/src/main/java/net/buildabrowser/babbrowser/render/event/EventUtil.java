package net.buildabrowser.babbrowser.render.event;

import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent;

public final class EventUtil {
  
  private EventUtil() {}

  public static boolean aabbZeroAdjusted(LayoutFragment fragment, float posX, float posY) {
    return
      posX >= 0
      && posX < fragment.borderWidth()
      && posY >= 0
      && posY < fragment.borderHeight();
  }

  public static void forwardElementEvent(
    MouseEvent mouseEvent, BoxFragment fragment, float posX, float posY
  ) {
    // TODO: Will need replaced with a proper MouseEvent
    System.out.println(fragment.box().element());
    EventDispatcher.dispatch((PointerEvent) () -> "click", fragment.box().element());
  }

  public static void forwardElementEvent(
    MouseEvent mouseEvent, ManagedBoxFragment fragment, TextFragment textFragment, float relX, float relY
  ) {
    // TODO: Handle things like text selection
    forwardElementEvent(mouseEvent, fragment, relX, relY);
  }

}
