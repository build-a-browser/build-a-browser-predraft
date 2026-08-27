package net.buildabrowser.babbrowser.renderer.event;

import java.util.concurrent.CompletableFuture;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
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
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;

public final class EventUtil {
  
  private EventUtil() {}

  public static boolean aabb(
    LayoutFragment fragment,
    float posX, float posY
  ) {
    if (fragment instanceof PosRefBoxFragment) return false;

    float layerX = fragment.layerX(Measurement.BORDER);
    float layerY = fragment.layerY(Measurement.BORDER);
    return aabb(fragment, posX, posY, layerX, layerY);
  }
  
  private static boolean aabb(
    LayoutFragment fragment,
    float posX, float posY,
    float layerX, float layerY
  ) {
    return
      posX >= layerX
      && posX < layerX + fragment.width(Measurement.BORDER)
      && posY >= layerY
      && posY < layerY + fragment.height(Measurement.BORDER);
  }

  public static EventHandlerResponse forwardElementEvent(
    EventContext eventContext,
    RendererMouseEvent mouseEvent,
    BoxFragment<?> fragment,
    float posX, float posY
  ) {
    return forwardElementEvent(
      eventContext, mouseEvent,
      fragment, fragment, posX, posY);
  }
  
  public static EventHandlerResponse forwardElementEvent(
    EventContext eventContext,
    RendererMouseEvent mouseEvent,
    BoxFragment<?> refFragment,
    LayoutFragment targetFragment,
    float posX, float posY
  ) {
    Box relatedBox = refFragment.box();
    while (
      relatedBox instanceof AnonymousElementBoxImp anonBox
    ) relatedBox = anonBox.parentBox();
    if (!(
      relatedBox instanceof ElementBox elementBox
    )) {
      return EventHandlerResponse.UNHANDLED;
    }
    Element element = elementBox.element();
    byte modifiers = mouseEvent.modifiers();
    Event event = switch (mouseEvent.event()) {
      case DOWN -> PointerEvent.create("mousedown", modifiers, posX, posY);
      case UP -> PointerEvent.create("mouseup", modifiers, posX, posY);
      case CLICK -> PointerEvent.create("click", modifiers, posX, posY);
      case MOVE -> PointerEvent.create("mousemove", modifiers, posX, posY);
      default -> null;
    };
    
    return forwardElementEvent(
      eventContext, event,
      element, refFragment, targetFragment);
  }

  public static EventHandlerResponse forwardElementEvent(
    EventContext eventContext,
    Event event, Element element
  ) {
    return forwardElementEvent(
      eventContext, event, element, null, null);
  }
  
  public static EventHandlerResponse forwardElementEvent(
    EventContext eventContext,
    Event event, Element element,
    BoxFragment<?> refFragment,
    LayoutFragment targetFragment
  ) {
    // TODO: Proper handling for null element
    if (event == null || element == null) {
      return EventHandlerResponse.UNHANDLED;
    }

    boolean contextPreventDefault = eventContext.isPreventDefault();
    CompletableFuture<SyncEventHandlerResponse> future = new CompletableFuture<>();
    HTMLDocument document = (HTMLDocument) element.nodeDocument();
    EventLoop.queueGlobalTask(
      TaskSource.USER_INTERACTION,
      document.nodeNavigable().activeWindow(),
      () -> {
        DocumentChangeListener changeListener = element.nodeDocument().changeListener();
        boolean allowDefault = true;
        if (
          targetFragment != null
          && changeListener instanceof RendererDocumentChangeListener rendererListener
        ) {
          allowDefault = rendererListener.onFragmentEventEarly(
            element, event, refFragment, targetFragment, allowDefault);
        }
        if (allowDefault) {
          allowDefault = changeListener.onElementEventEarly(
            element, event, true);
        }
        if (allowDefault) {
          allowDefault = EventDispatcher.dispatch(event, element);
        }
        allowDefault &= !contextPreventDefault;
        if (
          targetFragment != null
          && changeListener instanceof RendererDocumentChangeListener rendererListener
        ) {
          allowDefault = rendererListener.onFragmentEvent(
            element, event, refFragment, targetFragment, allowDefault);
        }
        allowDefault = changeListener.onElementEvent(
          element, event, allowDefault);
        SyncEventHandlerResponse response = allowDefault ?
          EventHandlerResponse.PERFORM_DEFAULT :
          EventHandlerResponse.HANDLED;
        future.complete(response);
      });

    return new AsyncEventHandlerResponse(future);
  }

}
