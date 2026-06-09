package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FlowRootBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class FlowRootContent implements BoxContent {

  private final ElementBox rootBox;

  private final FlowBlockLayout blockLayout;
  private final FlowInlineLayout inlineLayout;
  private final FloatTracker floatTracker;

  private ManagedBoxFragment<?> rootFragment;

  public FlowRootContent(ElementBox box) {
    this.rootBox = box;
    this.blockLayout = new FlowBlockLayout(this);
    this.inlineLayout = new FlowInlineLayout(this);
    this.floatTracker = FloatTracker.createForFlow(() -> blockLayout.activeContext());
  }

  @Override
  public FlowRootBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    floatTracker.reset();

    blockLayout.reset(rootBox, widthConstraint, heightConstraint);
    inlineLayout.reset();
    
    blockLayout.addChildrenToBlock(rootBox, widthConstraint, heightConstraint);

    this.rootFragment = blockLayout.close(widthConstraint, heightConstraint);
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
  public void positionLayers(float layerX, float layerY) {
    FlowLayerPositioning.positionLayers(
      layerX, layerY, rootFragment, floatTracker);
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }

  FlowBlockLayout blockLayout() {
    return this.blockLayout;
  }

  FlowInlineLayout inlineLayout() {
    return this.inlineLayout;
  }

  FloatTracker floatTracker() {
    return this.floatTracker;
  }

}
