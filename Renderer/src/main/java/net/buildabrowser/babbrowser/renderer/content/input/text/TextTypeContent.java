package net.buildabrowser.babbrowser.renderer.content.input.text;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.TextController;
import net.buildabrowser.babbrowser.renderer.content.input.InstrinsicSizedInputTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.TextInputFocusEventHandler;

public class TextTypeContent extends InstrinsicSizedInputTypeContent {

  public static final String PLACEHOLDER_CHARACTER = "a";

  private static TextInputFocusEventHandler TEXT_INPUT_FOCUS_EVENT_HANDLER = new TextInputFocusEventHandler();

  private final TextController textController;

  public TextTypeContent(
    HTMLInputElement element,
    boolean isHidden
  ) {
    this.textController = new InputTextController(
      element, isHidden);
  }

  public TextController textController() {
    return this.textController;
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
