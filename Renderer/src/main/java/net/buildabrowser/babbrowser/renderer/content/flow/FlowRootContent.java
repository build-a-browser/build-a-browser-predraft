package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlowRootContent implements BoxContent {

  public static final FlowRootContent INSTANCE = new FlowRootContent();

  private FlowRootContent() {}

  @Override
  public FlowRootBoxFragment layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    FlowContext flowContext = new FlowContext();
    FlowBlockLayout blockLayout = flowContext.blockLayout();
    FloatTracker floatTracker = flowContext.floatTracker();
    
    blockLayout.setup(rootBox, widthConstraint, heightConstraint);
    blockLayout.addChildrenToBlock(rootBox, widthConstraint, heightConstraint);

    ManagedBoxFragment<?> rootFragment = blockLayout.close(widthConstraint, heightConstraint);
    rootFragment.setPos(0, 0);

    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createFlowRootBoxFragment(
      rootFragment.width(Measurement.CONTENT),
      rootFragment.height(Measurement.CONTENT),
      rootFragment.inkWidth(Measurement.CONTENT),
      rootFragment.inkHeight(Measurement.CONTENT),
      rootBox, rootFragment, floatTracker.allFloats());
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    FlowRootBoxFragment flowFragment = (FlowRootBoxFragment) fragment;
    flowFragment.setLayerPos(layerX, layerY);
    ManagedBoxFragment<?> rootFragment = flowFragment.rootFragment();
    FlowLayerPositioning.positionLayers(
      layerX, layerY, rootFragment, flowFragment.floats());
  }

  public static FlowRootContent get() {
    return INSTANCE;
  }

}
