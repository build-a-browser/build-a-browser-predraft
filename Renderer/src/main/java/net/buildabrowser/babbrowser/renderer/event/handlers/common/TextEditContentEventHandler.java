package net.buildabrowser.babbrowser.renderer.event.handlers.common;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;
import net.buildabrowser.babbrowser.renderer.input.TextController;

public final class TextEditContentEventHandler {

  private TextEditContentEventHandler() {}
  
  public static EventHandlerResponse handleKeyboardEvent(
    TextController controller,
    RendererKeyboardEvent event,
    FontMetrics fontMetrics,
    float contentWidth,
    float contentHeight
  ) {
    // TODO: Support more keys like ctrl, insert, and support text selection
    if (event.code().equals(RendererKeyboardEvent.KEY_TAB)) {
      return EventHandlerResponse.PERFORM_DEFAULT;
    } else if (
      event.type().equals(KeyboardEventType.KEY_DOWN)
    ) {
      switch (event.code()) {
        case RendererKeyboardEvent.KEY_BACKSPACE -> controller.backspace();
        case RendererKeyboardEvent.KEY_LEFT_ARROW -> controller.moveCursorForward(-1);
        case RendererKeyboardEvent.KEY_RIGHT_ARROW -> controller.moveCursorForward(1);
        case RendererKeyboardEvent.KEY_UP_ARROW -> controller.moveCursorDownward(-1);
        case RendererKeyboardEvent.KEY_DOWN_ARROW -> controller.moveCursorDownward(1);
        case RendererKeyboardEvent.KEY_HOME -> moveToHomeOrTop(controller, event);
        case RendererKeyboardEvent.KEY_END -> moveToBottomOrEnd(controller, event);
        case RendererKeyboardEvent.KEY_PAGE_UP -> controller.movePageUp(contentHeight);
        case RendererKeyboardEvent.KEY_PAGE_DOWN -> controller.movePageDown(contentHeight);
        case RendererKeyboardEvent.KEY_DELETE -> controller.delete();
        case RendererKeyboardEvent.KEY_INSERT -> controller.toggleInsertMode();
        case RendererKeyboardEvent.KEY_ENTER -> submitOrNewline(controller);
        default -> {}
      }
      controller.scrollToCursor(contentWidth, contentHeight);
      return EventHandlerResponse.HANDLED;
    } else if (event.type().equals(KeyboardEventType.KEY_PRESS)) {
      controller.insertOrReplaceText(event.key());
      controller.scrollToCursor(contentWidth, contentHeight);
      return EventHandlerResponse.HANDLED;
    } else {
      return EventHandlerResponse.HANDLED;
    }
  }

  private static void moveToHomeOrTop(
    TextController controller,
    RendererKeyboardEvent event
  ) {
    if (event.ctrlKey()) {
      controller.moveTop();
    } else {
      controller.moveHome();
    }
  }

  private static void moveToBottomOrEnd(
    TextController controller,
    RendererKeyboardEvent event
  ) {
    if (event.ctrlKey()) {
      controller.moveBottom();
    } else {
      controller.moveEnd();
    }
  }

  private static void submitOrNewline(
    TextController controller
  ) {
    if (controller.isMultiLine()) {
      controller.insertText("\n");
    } else {
      controller.submit();
    }
  }

}
