package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.EventDispatcher;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.input.FocusOptions;
import net.buildabrowser.babbrowser.renderer.composite.CompositeEventsDispatcher;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;

public class HTMLEventForwardingTarget implements EventForwardingTarget {
  
  private final EventContext eventContext;

  private final HTMLCompositeLayers compositeLayers;
  private final FocusManager focusManager;
  private final SlotFamily<HTMLElement, ElementContext> elementContexts;

  public HTMLEventForwardingTarget(
    EventContext eventContext,
    HTMLDocument document,
    HTMLCompositeLayers compositeLayers,
    SlotFamily<HTMLElement, ElementContext> elementContexts
  ) {
    this.eventContext = eventContext;
    this.compositeLayers = compositeLayers;
    this.focusManager = document.focusManager();
    this.elementContexts = elementContexts;
  }

  @Override
  public EventHandlerResponse forwardEvent(RendererMouseEvent mouseEvent) {
    // I'm not putting this on the event loop until the spec's dispatcher runs, because
    // scroll bars need to remain responsive even while something like layout is running
    // TODO: There *was* a race condition being caused by this, but I can't consistently
    // reproduce it, so I can't debug it
    EventHandlerResponse response = compositeLayers.withFrontLayer(frontLayer ->
      CompositeEventsDispatcher.dispatchMouseEvent(
        eventContext, frontLayer, mouseEvent,
        mouseEvent.winX(), mouseEvent.winY()));
    if (response == null) return EventHandlerResponse.UNHANDLED;
    return response;
  }

  @Override
  public EventHandlerResponse forwardEvent(RendererKeyboardEvent keyEvent) {
    EventHandlerResponse response = EventHandlerResponse.UNHANDLED;
    if (
      focusManager.focused() instanceof HTMLElement htmlElement
    ) {
      ElementContext context = elementContexts.get(htmlElement);
      if (context.box() != null) {
        response = context.box().content().withFocusEventHandler(
          context.box(),
          (feh, c) -> feh.handleKeyboardEvent(
            eventContext, context.box(), c, keyEvent));
      }
    }
    
    return response.thenDefault(syncResponse -> {
      if (isFocusKey(keyEvent)) {
        cycleFocus(keyEvent);
        return EventHandlerResponse.HANDLED;
      } else if (isActivationKey(keyEvent)) {
        triggerActivation();
        return EventHandlerResponse.HANDLED;
      }

      return syncResponse;
    });
  }

  private boolean isFocusKey(RendererKeyboardEvent keyEvent) {
    return keyEvent.type().equals(KeyboardEventType.KEY_DOWN)
    && keyEvent.code().equals(RendererKeyboardEvent.KEY_TAB);
  }

  private void cycleFocus(RendererKeyboardEvent keyEvent) {
    FocusOptions focusOptions = new FocusOptions();
    focusOptions.focusVisible = true;
    if (keyEvent.shiftKey()) {
      focusManager.focusPrevious(focusOptions);
    } else {
      focusManager.focusNext(focusOptions);
    }
  }

  private boolean isActivationKey(RendererKeyboardEvent keyEvent) {
    return keyEvent.type().equals(KeyboardEventType.KEY_DOWN)
    && (
      keyEvent.code().equals(RendererKeyboardEvent.KEY_ENTER)
      || keyEvent.code().equals(RendererKeyboardEvent.KEY_SPACE));
  }

  private void triggerActivation() {
    Node currentNode = focusManager.focused();
    while (
      currentNode != null
      && !(currentNode instanceof ActivationTarget)
    ) {
      currentNode = currentNode.parentNode();
    }
    if (currentNode == null) return;
    // TODO: Need to add dummy pointer data
    EventDispatcher.dispatch(PointerEvent.createGeneric("click"), currentNode);
  }

}
