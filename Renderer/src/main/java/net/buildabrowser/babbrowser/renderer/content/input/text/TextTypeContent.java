package net.buildabrowser.babbrowser.renderer.content.input.text;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InstrinsicSizedInputTypeContent;
import net.buildabrowser.babbrowser.renderer.event.ContentEventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.TextInputContentEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.input.TextController;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class TextTypeContent extends InstrinsicSizedInputTypeContent {

  public static final String PLACEHOLDER_CHARACTER = "a";

  private static TextInputContentEventHandler TEXT_INPUT_FOCUS_EVENT_HANDLER = new TextInputContentEventHandler();

  private final TextController textController;

  public TextTypeContent(
    HTMLInputElement element,
    boolean isHidden
  ) {
    this.textController = new InputTextController(
      element, isHidden);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FontMetrics metrics = rootBox.layoutContext().font().metrics();
    textController.updateMetrics(metrics);
    return super.layout(rootBox, widthConstraint, heightConstraint);
  }

  public TextController textController() {
    return this.textController;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BoxContent> EventHandlerResponse withContentEventHandler(
    ElementBox box,
    ContentEventHandlerFunc<T> withHandlerFunc
  ) {
    return withHandlerFunc.apply(
      (ContentEventHandler<T>) TEXT_INPUT_FOCUS_EVENT_HANDLER,
      (T) this);
  }
  
}
