package net.buildabrowser.babbrowser.render.content.flow;

import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

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

    float desiredWidth = rootFragment.contentWidth();
    float desiredHeight = Math.max(rootFragment.contentHeight(), floatTracker.contentHeight());
    float inkWidth = rootFragment.inkWidth(Measurement.CONTENT);
    float inkHeight = Math.max(
      rootFragment.inkHeight(Measurement.CONTENT),
      floatTracker.contentHeight()); // TODO
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, desiredWidth);
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, desiredHeight);
    
    return new FlowRootBoxFragment(
      usedWidth, usedHeight,
      inkWidth, inkHeight,
      rootBox, rootFragment, floatTracker.allFloats());
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    recursePositionLayers(
      layerX, layerY,
      rootFragment, rootBox.stackingContext());

    float offsetX = layerX + (rootFragment.contentX() - rootFragment.borderX());
    float offsetY = layerY + (rootFragment.contentY() - rootFragment.borderY());
    for (LayoutFragment floatFragment: floatTracker.allFloats()) {
      recursePositionLayers(
        offsetX + floatFragment.borderX(),
        offsetY + floatFragment.borderY(),
        floatFragment, rootBox.stackingContext());
    }
  }

  @Override
  public EventHandler eventHandler() {
    return EVENT_HANDLER;
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

  private void recursePositionLayers(
    float layerX, float layerY, LayoutFragment fragment, StackingContext refContext
  ) {
    switch (fragment) {
      case TextFragment _ -> {}
      case PosRefBoxFragment posRef -> {
        posRef.box().dimensions().setStaticPosition(layerX, layerY);
      }
      case LineBoxFragment lineBoxFragment -> recursePositionLineBoxFragment(
        layerX, layerY, refContext, lineBoxFragment);
      case ManagedBoxFragment boxFragment -> recursePositionManagedBoxFragment(
        layerX, layerY, fragment, refContext, boxFragment);
      case UnmanagedBoxFragment boxFragment -> recursePositionUnmanagedBoxFragment(
        layerX, layerY, refContext, boxFragment);

      default -> throw new UnsupportedOperationException("Don't recognize fragment type!");
    }
  }

  private void recursePositionLineBoxFragment(
    float layerX, float layerY, StackingContext refContext, LineBoxFragment lineBoxFragment
  ) {
    LayoutFragment child = lineBoxFragment.fragments();
    while (child != null) {
      recursePositionLayers(
        layerX + child.borderX(),
        layerY + child.borderY(),
        child, refContext);
      child = child.next();
    }
  }

  private void recursePositionManagedBoxFragment(
    float layerX, float layerY, LayoutFragment fragment, StackingContext refContext,
    ManagedBoxFragment boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      refContext = boxFragment.box().stackingContext();
      refContext.addFragment(layerX, layerY, boxFragment);
    }
    
    LayoutFragment child = boxFragment.fragments();
    float offsetX = layerX + (fragment.contentX() - fragment.borderX());
    float offsetY = layerY + (fragment.contentY() - fragment.borderY());
    while (child != null) {
      recursePositionLayers(
        offsetX + child.borderX(),
        offsetY + child.borderY(),
        child, refContext);
      child = child.next();
    }
  }

  private void recursePositionUnmanagedBoxFragment(
    float layerX, float layerY, StackingContext refContext, UnmanagedBoxFragment boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      refContext = boxFragment.box().stackingContext();
      refContext.addFragment(layerX, layerY, boxFragment);
    }
    boxFragment.box().content().positionLayers(layerX, layerY);
  }

  // For testing
  public ManagedBoxFragment rootFragment() {
    return this.rootFragment;
  }

}
