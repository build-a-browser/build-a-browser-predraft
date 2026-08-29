package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.HiddenInputEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.HiddenInputBoxPainter;

public class DefaultHiddenInputFragment extends HiddenInputFragment {

  private static final HiddenInputBoxPainter HIDDEN_INPUT_BOX_PAINTER = new HiddenInputBoxPainter();
  private static final HiddenInputEventHandler HIDDEN_INPUT_EVENT_HANDLER = new HiddenInputEventHandler();

  public DefaultHiddenInputFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box
  ) {
    super(width, height, inkWidth, inkHeight, box);
  }

  @Override
  protected BoxPainter<HiddenInputFragment> painter() {
    return HIDDEN_INPUT_BOX_PAINTER;
  }

  @Override
  protected EventHandler<HiddenInputFragment> eventHandler() {
    return HIDDEN_INPUT_EVENT_HANDLER;
  }
  
  @Override
  public DefaultHiddenInputFragment newCopy() {
    return new DefaultHiddenInputFragment(
      width(Measurement.CONTENT), height(Measurement.CONTENT),
      inkWidth(Measurement.CONTENT), inkHeight(Measurement.CONTENT),
      box());
  }
  
}
