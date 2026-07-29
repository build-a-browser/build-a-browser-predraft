package net.buildabrowser.babbrowser.renderer.event.handlers.common;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.content.common.TextController;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;

public final class TextEditFocusEventHandler {

  private TextEditFocusEventHandler() {}
  
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
        case RendererKeyboardEvent.KEY_BACKSPACE -> backspace(controller);
        case RendererKeyboardEvent.KEY_LEFT_ARROW -> moveCursor(controller, -1);
        case RendererKeyboardEvent.KEY_RIGHT_ARROW -> moveCursor(controller, 1);
        case RendererKeyboardEvent.KEY_HOME -> moveHome(controller);
        case RendererKeyboardEvent.KEY_END -> moveEnd(controller);
        case RendererKeyboardEvent.KEY_DELETE -> delete(controller);
        case RendererKeyboardEvent.KEY_INSERT -> toggleInsertMode(controller);
        case RendererKeyboardEvent.KEY_ENTER -> controller.submit();
        default -> {}
      }
      scrollToCursor(controller, fontMetrics, contentWidth, contentHeight);
      return EventHandlerResponse.HANDLED;
    } else if (event.type().equals(KeyboardEventType.KEY_PRESS)) {
      insertOrReplaceText(controller, event.key());
      scrollToCursor(controller, fontMetrics, contentWidth, contentHeight);
      return EventHandlerResponse.HANDLED;
    } else {
      return EventHandlerResponse.HANDLED;
    }
  }

  private static void insertOrReplaceText(TextController controller, String text) {
    if (controller.isReplaceMode()) {
      replaceText(controller, text);
    } else {
      insertText(controller, text);
    }
  }

  private static void insertText(TextController controller, String text) {
    int cursorX = controller.cursorX();
    // TODO: Should probably use a StringBuilder instead
    controller.setValue(controller.value().substring(0, cursorX) + text + controller.value().substring(cursorX));
    controller.setCursorX(cursorX + text.length());
  }

  private static void replaceText(TextController controller, String text) {
    int cursorX = controller.cursorX();
    if (cursorX == controller.value().length()) {
      insertText(controller, text);
      return;
    }
    controller.setValue(controller.value().substring(0, cursorX) + text + controller.value().substring(cursorX + 1));
    controller.setCursorX(cursorX + text.length());
  }

  private static void backspace(TextController controller) {
    int cursorX = controller.cursorX();
    if (cursorX == 0) return;
    controller.setValue(controller.value().substring(0, cursorX - 1) + controller.value().substring(cursorX));
    controller.setCursorX(cursorX - 1);
  }

  private static void moveCursor(TextController controller, int i) {
    controller.setCursorX(mathClamp(
      controller.cursorX() + i,
      0, controller.value().length()));
  }

  private static void moveHome(TextController controller) {
    controller.setCursorX(0);
  }

  private static void moveEnd(TextController controller) {
    controller.setCursorX(controller.value().length());
  }

  private static void delete(TextController controller) {
    int cursorX = controller.cursorX();
    if (cursorX == controller.value().length()) return;
    controller.setValue(controller.value().substring(0, cursorX) + controller.value().substring(cursorX + 1));
  }

  private static void toggleInsertMode(TextController controller) {
    controller.setIsReplaceMode(!controller.isReplaceMode());
  }

  private static void scrollToCursor(
    TextController controller,
    FontMetrics fontMetrics,
    float contentWidth,
    float contentHeight
  ) {    
    String value = controller.value();
    float adjustedWidth = Math.max(0, contentWidth - TextEditPainter.HORIZONTAL_PADDING);
    float scrollX = controller.scrollX();
    float valueWidth = fontMetrics.stringWidth(value);
    float letterWidth = controller.cursorX() == value.length() ?
      fontMetrics.stringWidth(TextTypeContent.PLACEHOLDER_CHARACTER) :
      fontMetrics.stringWidth(controller.value().substring(
        controller.cursorX(), controller.cursorX() + 1));
    float toCursorWidth = fontMetrics.stringWidth(
      value.substring(0, controller.cursorX()));
    float lowerBound = Math.max(0, toCursorWidth + letterWidth - adjustedWidth);
    float upperBound = toCursorWidth - letterWidth;
    if (scrollX > upperBound) {
      scrollX = upperBound;
    }
    if (scrollX < lowerBound) {
      scrollX = lowerBound;
    }
    scrollX = Math.max(0, Math.min(scrollX, valueWidth - adjustedWidth + letterWidth));
    controller.setScrollX(scrollX);
  }

}
