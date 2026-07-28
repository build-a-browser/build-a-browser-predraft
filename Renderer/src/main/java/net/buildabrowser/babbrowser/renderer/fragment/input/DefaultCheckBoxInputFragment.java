package net.buildabrowser.babbrowser.renderer.fragment.input;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.GenericEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.CheckBoxInputBoxPainter;

public class DefaultCheckBoxInputFragment extends CheckBoxInputFragment {

  private static final CheckBoxInputBoxPainter CHECK_BOX_INPUT_BOX_PAINTER = new CheckBoxInputBoxPainter();
  private static final GenericEventHandler<CheckBoxInputFragment> CHECK_BOX_INPUT_EVENT_HANDLER = new GenericEventHandler<>();

  public DefaultCheckBoxInputFragment(
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
  protected BoxPainter<CheckBoxInputFragment> painter() {
    return CHECK_BOX_INPUT_BOX_PAINTER;
  }

  @Override
  protected EventHandler<CheckBoxInputFragment> eventHandler() {
    return CHECK_BOX_INPUT_EVENT_HANDLER; 
  }
  
}
