package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.GenericEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.RadioBoxInputBoxPainter;

public class DefaultRadioBoxInputFragment extends RadioBoxInputFragment {

  private static final RadioBoxInputBoxPainter RADIO_BOX_INPUT_BOX_PAINTER = new RadioBoxInputBoxPainter();
  private static final GenericEventHandler<RadioBoxInputFragment> RADIO_BOX_INPUT_EVENT_HANDLER = new GenericEventHandler<>();

  public DefaultRadioBoxInputFragment(
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
  protected BoxPainter<RadioBoxInputFragment> painter() {
    return RADIO_BOX_INPUT_BOX_PAINTER;
  }

  @Override
  protected EventHandler<RadioBoxInputFragment> eventHandler() {
    return RADIO_BOX_INPUT_EVENT_HANDLER; 
  }
  
}
