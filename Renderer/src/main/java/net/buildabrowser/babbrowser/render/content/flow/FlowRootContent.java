package net.buildabrowser.babbrowser.render.content.flow;

import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class FlowRootContent implements BoxContent {

  private static final EventHandler EVENT_HANDLER = new FlowRootEventHandler();

  private final ElementBox rootBox;

  private final FlowBlockLayout blockLayout;
  private final FlowInlineLayout inlineLayout;
  private final FloatTracker floatTracker;

  private ManagedBoxFragment rootFragment;

  public FlowRootContent(ElementBox box) {
    this.rootBox = box;
    this.blockLayout = new FlowBlockLayout(this);
    this.inlineLayout = new FlowInlineLayout(this);
    this.floatTracker = FloatTracker.createForFlow(() -> blockLayout.activeContext());
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    floatTracker.reset();

    blockLayout.reset(rootBox, widthConstraint, heightConstraint);
    inlineLayout.reset();
    
    blockLayout.addChildrenToBlock(rootBox, widthConstraint, heightConstraint);

    this.rootFragment = blockLayout.close(widthConstraint, heightConstraint);
    rootFragment.setPos(0, 0);

    return new FlowRootBoxFragment(
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
  public EventHandler eventHandler() {
    return EVENT_HANDLER;
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

  // For testing
  public ManagedBoxFragment rootFragment() {
    return this.rootFragment;
  }

}
