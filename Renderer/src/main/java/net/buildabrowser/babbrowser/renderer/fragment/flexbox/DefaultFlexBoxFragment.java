package net.buildabrowser.babbrowser.renderer.fragment.flexbox;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.flexbox.FlexBoxEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.flexbox.FlexBoxPainter;

public class DefaultFlexBoxFragment extends FlexBoxFragment {

  private static FlexBoxPainter FLEX_BOX_PAINTER = new FlexBoxPainter();
  private static FlexBoxEventHandler FLEX_BOX_EVENT_HANDLER = new FlexBoxEventHandler();

  public DefaultFlexBoxFragment(
    float usedWidth, float usedHeight,
    float inkWidth, float inkHeight,
    ElementBox box, UnmanagedBoxFragment<?> fragments
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight,
      box, fragments);
  }

  @Override
  protected BoxPainter<FlexBoxFragment> painter() {
    return FLEX_BOX_PAINTER;
  }

  @Override
  protected EventHandler<FlexBoxFragment> eventHandler() {
    return FLEX_BOX_EVENT_HANDLER;
  }
  
}
