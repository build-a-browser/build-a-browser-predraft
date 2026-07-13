package net.buildabrowser.babbrowser.renderer.event;

import java.util.concurrent.CompletableFuture;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse.AsyncEventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse.SyncEventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public final class EventUtil {
  
  private EventUtil() {}

  public static boolean aabb(
    BoxFragment<?> fragment,
    float posX, float posY
  ) {
    if (fragment instanceof PosRefBoxFragment) return false;

    float layerX = fragment.layerX(Measurement.BORDER);
    float layerY = fragment.layerY(Measurement.BORDER);
    return
      posX >= layerX
      && posX < layerX + fragment.width(Measurement.BORDER)
      && posY >= layerY
      && posY < layerY + fragment.height(Measurement.BORDER);
  }

  public static boolean aabb(
    BoxFragment<?> parentFragment,
    LayoutFragment thisFragment,
    float posX, float posY
  ) {
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
    RendererMouseEvent mouseEvent,
    BoxFragment<?> fragment,
    float posX, float posY
  ) {
    Box relatedBox = fragment.box();
    while (
      relatedBox instanceof AnonymousElementBoxImp anonBox
    ) relatedBox = anonBox.parentBox();
    if (!(
      relatedBox instanceof ElementBox elementBox
    )) {
      return EventHandlerResponse.UNHANDLED;
    }
    Element element = elementBox.element();
    Event event = switch (mouseEvent.event()) {
      case CLICK -> (PointerEvent) () -> "click";
      case MOVE -> (PointerEvent) () -> "mousemove";
      default -> null;
    };
    
    return forwardElementEvent(event, element);
  }

  public static EventHandlerResponse forwardElementEvent(
    RendererMouseEvent mouseEvent,
    ManagedBoxFragment<?> fragment,
    TextFragment textFragment,
    float relX, float relY
  ) {
    // TODO: Handle things like text selection
    return forwardElementEvent(mouseEvent, fragment, relX, relY);
  }
  
  public static EventHandlerResponse forwardElementEvent(
    Event event, Element element
  ) {
    if (event == null) {
      return EventHandlerResponse.UNHANDLED;
    }

    CompletableFuture<SyncEventHandlerResponse> future = new CompletableFuture<>();
    HTMLDocument document = (HTMLDocument) element.nodeDocument();
    EventLoop.queueGlobalTask(
      TaskSource.USER_INTERACTION,
      document.nodeNavigable().activeWindow(),
      () -> {
        boolean allowDefault = EventDispatcher.dispatch(event, element);
        allowDefault = element.nodeDocument().changeListener().onElementEvent(
          element, event, allowDefault);
        SyncEventHandlerResponse response = allowDefault ?
          EventHandlerResponse.PERFORM_DEFAULT :
          EventHandlerResponse.HANDLED;
        future.complete(response);
      });

    return new AsyncEventHandlerResponse(future);
  }

}
