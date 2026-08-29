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
    float firstBaseline, float lastBaseline,
    ElementBox box, UnmanagedBoxFragment<?> fragments
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
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

  @Override
  public DefaultFlexBoxFragment newCopy() {
    return new DefaultFlexBoxFragment(
      width(Measurement.CONTENT), height(Measurement.CONTENT),
      inkWidth(Measurement.CONTENT), inkHeight(Measurement.CONTENT),
      firstBaseline(Measurement.CONTENT), lastBaseline(Measurement.CONTENT),
      box(), (UnmanagedBoxFragment<?>) innerFragment());
  }
  
}
