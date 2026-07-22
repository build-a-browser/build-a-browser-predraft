package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.TextInputEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.TextInputBoxPainter;

public class DefaultTextInputFragment extends TextInputFragment {

  private static final TextInputBoxPainter TEXT_INPUT_BOX_PAINTER = new TextInputBoxPainter();
  private static final TextInputEventHandler TEXT_INPUT_EVENT_HANDLER = new TextInputEventHandler();

  public DefaultTextInputFragment(
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
  protected BoxPainter<TextInputFragment> painter() {
    return TEXT_INPUT_BOX_PAINTER;
  }

  @Override
  protected EventHandler<TextInputFragment> eventHandler() {
    return TEXT_INPUT_EVENT_HANDLER;
  }
  
}
