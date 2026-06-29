package net.buildabrowser.babbrowser.renderer.event.handlers.input;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.TextInputBoxPainter;

public class TextInputFocusEventHandler implements FocusEventHandler<TextTypeContent> {
  
  @Override
  public EventHandlerResponse handleKeyboardEvent(
    EventContext eventContext,
    ElementBox box,
    TextTypeContent content,
    RendererKeyboardEvent event
  ) {
    // TODO: Support more keys like ctrl, insert, and support text selection
    box.context().invalidate(InvalidationLevel.PAINT);
    if (event.code().equals(RendererKeyboardEvent.KEY_TAB)) {
      return EventHandlerResponse.PERFORM_DEFAULT;
    } else if (
      event.type().equals(KeyboardEventType.KEY_DOWN)
    ) {
      switch (event.code()) {
        case RendererKeyboardEvent.KEY_BACKSPACE -> backspace(content);
        case RendererKeyboardEvent.KEY_LEFT_ARROW -> moveCursor(content, -1);
        case RendererKeyboardEvent.KEY_RIGHT_ARROW -> moveCursor(content, 1);
        case RendererKeyboardEvent.KEY_HOME -> moveHome(content);
        case RendererKeyboardEvent.KEY_END -> moveEnd(content);
        case RendererKeyboardEvent.KEY_DELETE -> delete(content);
        case RendererKeyboardEvent.KEY_INSERT -> toggleInsertMode(content);
        default -> {}
      }
      scrollToCursor(box, content);
      return EventHandlerResponse.HANDLED;
    } else if (event.type().equals(KeyboardEventType.KEY_PRESS)) {
      insertOrReplaceText(content, event.key());
      scrollToCursor(box, content);
      return EventHandlerResponse.HANDLED;
    } else {
      return EventHandlerResponse.HANDLED;
    }
  }

  private void insertOrReplaceText(TextTypeContent content, String text) {
    if (content.isReplaceMode()) {
      replaceText(content, text);
    } else {
      insertText(content, text);
    }
  }

  private void insertText(TextTypeContent content, String text) {
    int cursorX = content.cursorX();
    // TODO: Should probably use a StringBuilder instead
    content.setValue(content.value().substring(0, cursorX) + text + content.value().substring(cursorX));
    content.setCursorX(cursorX + text.length());
  }

  private void replaceText(TextTypeContent content, String text) {
    int cursorX = content.cursorX();
    if (cursorX == content.value().length()) {
      insertText(content, text);
      return;
    }
    content.setValue(content.value().substring(0, cursorX) + text + content.value().substring(cursorX + 1));
    content.setCursorX(cursorX + text.length());
  }

  private void backspace(TextTypeContent content) {
    int cursorX = content.cursorX();
    if (cursorX == 0) return;
    content.setValue(content.value().substring(0, cursorX - 1) + content.value().substring(cursorX));
    content.setCursorX(cursorX - 1);
  }

  private void moveCursor(TextTypeContent content, int i) {
    content.setCursorX(mathClamp(
      content.cursorX() + i,
      0, content.value().length()));
  }

  private void moveHome(TextTypeContent content) {
    content.setCursorX(0);
  }

  private void moveEnd(TextTypeContent content) {
    content.setCursorX(content.value().length());
  }

  private void delete(TextTypeContent content) {
    int cursorX = content.cursorX();
    if (cursorX == content.value().length()) return;
    content.setValue(content.value().substring(0, cursorX) + content.value().substring(cursorX + 1));
  }

  private void toggleInsertMode(TextTypeContent content) {
    content.setIsReplaceMode(!content.isReplaceMode());
  }

  private void scrollToCursor(ElementBox scrollBox, TextTypeContent content) {
    BoxFragment<?> fragment = scrollBox.positioningFragment();
    if (fragment == null) return;
    
    String value = content.value();
    FontMetrics fontMetrics = scrollBox.layoutContext().font().metrics();
    float adjustedWidth = Math.max(0, fragment.width(Measurement.CONTENT) - TextInputBoxPainter.HORIZONTAL_PADDING);
    float scrollX = content.scrollX();
    float valueWidth = fontMetrics.stringWidth(value);
    float letterWidth = content.cursorX() == value.length() ?
      fontMetrics.stringWidth(TextTypeContent.PLACEHOLDER_CHARACTER) :
      fontMetrics.stringWidth(content.value().substring(
        content.cursorX(), content.cursorX() + 1));
    float toCursorWidth = fontMetrics.stringWidth(
      value.substring(0, content.cursorX()));
    float lowerBound = Math.max(0, toCursorWidth + letterWidth - adjustedWidth);
    float upperBound = toCursorWidth - letterWidth;
    if (scrollX > upperBound) {
      scrollX = upperBound;
    }
    if (scrollX < lowerBound) {
      scrollX = lowerBound;
    }
    scrollX = Math.max(0, Math.min(scrollX, valueWidth - adjustedWidth + letterWidth));
    content.setScrollX(scrollX);
  }

}
