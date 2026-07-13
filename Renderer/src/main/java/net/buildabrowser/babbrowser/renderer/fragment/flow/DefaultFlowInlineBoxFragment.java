package net.buildabrowser.babbrowser.renderer.fragment.flow;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.flow.FlowGenericEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowInlineBoxPainter;

public class DefaultFlowInlineBoxFragment extends FlowInlineBoxFragment {

  private static FlowInlineBoxPainter FLOW_INLINE_BOX_PAINTER = new FlowInlineBoxPainter();
  private static FlowGenericEventHandler<FlowInlineBoxFragment> FLOW_GENERIC_EVENT_HANDLER
    = new FlowGenericEventHandler<>();

  public DefaultFlowInlineBoxFragment(
    float usedWidth, float usedHeight,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, LayoutFragment fragments
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box, fragments);
  }

  @Override
  protected BoxPainter<FlowInlineBoxFragment> painter() {
    return FLOW_INLINE_BOX_PAINTER;
  }

  @Override
  protected EventHandler<FlowInlineBoxFragment> eventHandler() {
    return FLOW_GENERIC_EVENT_HANDLER;
  }
  
}
