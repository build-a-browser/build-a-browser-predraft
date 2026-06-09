package net.buildabrowser.babbrowser.renderer.fragment.flow;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.flow.FlowGenericEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowBlockBoxPainter;

public class DefaultFlowBlockBoxFragment extends FlowBlockBoxFragment {

  private static FlowBlockBoxPainter FLOW_BLOCK_BOX_PAINTER = new FlowBlockBoxPainter();
  private static FlowGenericEventHandler<FlowBlockBoxFragment> FLOW_GENERIC_EVENT_HANDLER
    = new FlowGenericEventHandler<>();

  public DefaultFlowBlockBoxFragment(
    float usedWidth, float usedHeight,
    float inkWidth, float inkHeight,
    ElementBox box, LayoutFragment fragments
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight,
      box, fragments);
  }

  @Override
  protected BoxPainter<FlowBlockBoxFragment> painter() {
    return FLOW_BLOCK_BOX_PAINTER;
  }

  @Override
  protected EventHandler<FlowBlockBoxFragment> eventHandler() {
    return FLOW_GENERIC_EVENT_HANDLER;
  }
  
}
