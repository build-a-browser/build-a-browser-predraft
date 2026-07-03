package net.buildabrowser.babbrowser.renderer.content.input.text;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.stringRepeat;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InstrinsicSizedInputTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.TextInputFocusEventHandler;

public class TextTypeContent extends InstrinsicSizedInputTypeContent {

  public static final String PLACEHOLDER_CHARACTER = "a";

  private static final String PASSWORD_CHARACTER = "\u2219";
  private static TextInputFocusEventHandler TEXT_INPUT_FOCUS_EVENT_HANDLER = new TextInputFocusEventHandler();

  private final HTMLInputElement element;
  private final boolean isHidden;

  private int cursorX = 0;
  private float scrollX = 0;
  private boolean isReplaceMode = false;

  public TextTypeContent(
    HTMLInputElement element,
    boolean isHidden
  ) {
    this.element = element;
    this.isHidden = isHidden;
  }

  public String value() {
    return element.value();
  }

  public void setValue(String value) {
    element.setValue(value);
  }

  public String displayValue() {
    if (isHidden) {
      int valueLen = value().length();
      return stringRepeat(PASSWORD_CHARACTER, valueLen);
    } else return value();
  }

  public int cursorX() {
    return this.cursorX;
  }

  public void setCursorX(int cursorX) {
    this.cursorX = cursorX;
  }

  public float scrollX() {
    return this.scrollX;
  }

  public void setScrollX(float scrollX) {
    this.scrollX = scrollX;
  }

  public boolean isReplaceMode() {
    return this.isReplaceMode;
  }

  public void setIsReplaceMode(boolean isReplaceMode) {
    this.isReplaceMode = isReplaceMode;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BoxContent> EventHandlerResponse withFocusEventHandler(
    ElementBox box,
    FocusEventHandlerFunc<T> withHandlerFunc
  ) {
    return withHandlerFunc.apply(
      (FocusEventHandler<T>) TEXT_INPUT_FOCUS_EVENT_HANDLER,
      (T) this);
  }
  
}
