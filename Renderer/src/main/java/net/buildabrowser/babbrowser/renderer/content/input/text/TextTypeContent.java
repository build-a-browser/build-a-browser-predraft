package net.buildabrowser.babbrowser.renderer.content.input.text;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InstrinsicSizedInputTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.TextInputFocusEventHandler;

public class TextTypeContent extends InstrinsicSizedInputTypeContent {

  public static final String PLACEHOLDER_CHARACTER = "a";

  private static TextInputFocusEventHandler TEXT_INPUT_FOCUS_EVENT_HANDLER = new TextInputFocusEventHandler();

  private int cursorX = 0;
  private float scrollX = 0;
  private boolean isReplaceMode = false;

  public TextTypeContent(ElementBox rootBox) {
    super(rootBox);
  }

  public String value() {
    return element().value();
  }

  public void setValue(String value) {
    element().setValue(value);
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

  private HTMLInputElement element() {
    return (HTMLInputElement) rootBox().element();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BoxContent> EventHandlerResponse withFocusEventHandler(
    FocusEventHandlerFunc<T> withHandlerFunc
  ) {
    return withHandlerFunc.apply(
      (FocusEventHandler<T>) TEXT_INPUT_FOCUS_EVENT_HANDLER,
      (T) this);
  }
  
}
