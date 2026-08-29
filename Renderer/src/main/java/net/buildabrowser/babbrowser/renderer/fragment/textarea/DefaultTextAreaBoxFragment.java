package net.buildabrowser.babbrowser.renderer.fragment.textarea;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.textarea.TextAreaEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.textarea.TextAreaBoxPainter;

public class DefaultTextAreaBoxFragment extends TextAreaBoxFragment {

  private static final TextAreaBoxPainter TEXT_AREA_BOX_PAINTER = new TextAreaBoxPainter();
  private static final TextAreaEventHandler TEXT_AREA_EVENT_HANDLER = new TextAreaEventHandler();

  public DefaultTextAreaBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box
  ) {
    super(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box);
  }

  @Override
  protected BoxPainter<TextAreaBoxFragment> painter() {
    return TEXT_AREA_BOX_PAINTER;
  }

  @Override
  protected EventHandler<TextAreaBoxFragment> eventHandler() {
    return TEXT_AREA_EVENT_HANDLER;
  }

  @Override
  public DefaultTextAreaBoxFragment newCopy() {
    return new DefaultTextAreaBoxFragment(
      width(Measurement.CONTENT), height(Measurement.CONTENT),
      inkWidth(Measurement.CONTENT), inkHeight(Measurement.CONTENT),
      firstBaseline(Measurement.CONTENT), lastBaseline(Measurement.CONTENT),
      box());
  }
  
}
