package net.buildabrowser.babbrowser.render.event;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public final class EventUtil {
  
  private EventUtil() {}

  public static boolean aabbZeroAdjusted(LayoutFragment fragment, float posX, float posY) {
    return
      posX >= 0
      && posX < fragment.borderWidth()
      && posY >= 0
      && posY < fragment.borderHeight();
  }

  public static EventHandlerResponse forwardElementEvent(
    RendererMouseEvent mouseEvent, BoxFragment fragment, float posX, float posY
  ) {
    Box relatedBox = fragment.box();
    while (
      relatedBox instanceof AnonymousElementBoxImp anonBox
    ) relatedBox = anonBox.parentBox();
    if (!(relatedBox instanceof ElementBox elementBox)) return EventHandlerResponse.UNHANDLED;
    Element element = elementBox.element();
    Event event = switch (mouseEvent.event()) {
      case CLICK -> (PointerEvent) () -> "click";
      case MOVE -> (PointerEvent) () -> "mousemove";
      default -> null;
    };
    if (event == null) return EventHandlerResponse.UNHANDLED;
    // TODO: Will need replaced with a proper MouseEvent
    element.nodeDocument().changeListener().onElementEvent(element, event);
    EventDispatcher.dispatch(event, element);

    return EventHandlerResponse.PERFORM_DEFAULT;
  }

  public static EventHandlerResponse forwardElementEvent(
    RendererMouseEvent mouseEvent, ManagedBoxFragment fragment, TextFragment textFragment, float relX, float relY
  ) {
    // TODO: Handle things like text selection
    return forwardElementEvent(mouseEvent, fragment, relX, relY);
  }

}
