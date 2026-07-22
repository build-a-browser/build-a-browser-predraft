package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.input.ButtonInputEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.ButtonInputBoxPainter;

public class DefaultButtonInputFragment extends ButtonInputFragment {

  private static final ButtonInputBoxPainter BUTTON_INPUT_BOX_PAINTER = new ButtonInputBoxPainter();
  private static final ButtonInputEventHandler BUTTON_INPUT_EVENT_HANDLER = new ButtonInputEventHandler();

  public DefaultButtonInputFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box,
    UnmanagedBoxFragment<?> innerFragment
  ) {
    super(
      width, height, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, innerFragment);
  }

  @Override
  protected BoxPainter<ButtonInputFragment> painter() {
    return BUTTON_INPUT_BOX_PAINTER;
  }

  @Override
  protected EventHandler<ButtonInputFragment> eventHandler() {
    return BUTTON_INPUT_EVENT_HANDLER; 
  }
  
}
