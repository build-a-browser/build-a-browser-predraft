package net.buildabrowser.babbrowser.renderer.fragment.flow;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.flow.FlowRootEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.flow.FlowRootBoxPainter;

public class DefaultFlowRootBoxFragment extends FlowRootBoxFragment {

  private static FlowRootBoxPainter FLOW_ROOT_BOX_PAINTER = new FlowRootBoxPainter();
  private static FlowRootEventHandler FLOW_ROOT_EVENT_HANDLER = new FlowRootEventHandler();

  public DefaultFlowRootBoxFragment(
    float usedWidth, float usedHeight,
    float inkWidth, float inkHeight,
    ElementBox rootBox, ManagedBoxFragment<?> rootFragment,
    List<BoxFragment<?>> floats
  ) {
    super(
      usedWidth, usedHeight, inkWidth, inkHeight,
      rootBox, rootFragment, floats);
  }

  @Override
  protected BoxPainter<FlowRootBoxFragment> painter() {
    return FLOW_ROOT_BOX_PAINTER;
  }

  @Override
  protected EventHandler<FlowRootBoxFragment> eventHandler() {
    return FLOW_ROOT_EVENT_HANDLER;
  }
  
}
