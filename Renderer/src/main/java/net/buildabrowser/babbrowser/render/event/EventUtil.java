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
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public final class EventUtil {
  
  private EventUtil() {}

  public static boolean aabb(BoxFragment fragment, float posX, float posY) {
    if (fragment instanceof PosRefBoxFragment) return false;

    float layerX = fragment.layerX(Measurement.BORDER);
    float layerY = fragment.layerY(Measurement.BORDER);
    return
      posX >= layerX
      && posX < layerX + fragment.width(Measurement.BORDER)
      && posY >= layerY
      && posY < layerY + fragment.height(Measurement.BORDER);
  }

  public static boolean aabb(BoxFragment parentFragment, LayoutFragment thisFragment, float posX, float posY) {
    if (thisFragment instanceof BoxFragment boxFragment) {
      return aabb(boxFragment, posX, posY);
    }

    float docX = parentFragment.layerX(Measurement.CONTENT) + thisFragment.posX(Measurement.BORDER);
    float docY = parentFragment.layerY(Measurement.CONTENT) + thisFragment.posY(Measurement.BORDER);
    return
      posX >= docX
      && posX < docX + thisFragment.width(Measurement.BORDER)
      && posY >= docY
      && posY < docY + thisFragment.height(Measurement.BORDER);
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
