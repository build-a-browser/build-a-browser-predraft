package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContentPainter.FlowRootBoxPainter;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;

public class FlowRootContent implements BoxContent {

  private final ElementBox rootBox;

  private final FlowBlockLayout blockLayout;
  private final FlowInlineLayout inlineLayout;
  private final FloatTracker floatTracker;

  private ManagedBoxFragment rootFragment;

  public FlowRootContent(ElementBox box) {
    this.rootBox = box;
    this.blockLayout = new FlowBlockLayout(this);
    this.inlineLayout = new FlowInlineLayout(this);
    this.floatTracker = FloatTracker.create();
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    floatTracker.reset();

    blockLayout.reset(rootBox, widthConstraint, heightConstraint);
    blockLayout.addChildrenToBlock(rootBox, widthConstraint, heightConstraint);

    this.rootFragment = blockLayout.close(widthConstraint, heightConstraint);
    rootFragment.setPos(0, 0);

    float desiredHeight = Math.max(rootFragment.contentHeight(), floatTracker.contentHeight());
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, rootFragment.contentWidth());
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, desiredHeight);
    
    UnmanagedBoxFragment wrapperFragment = new UnmanagedBoxFragment(usedWidth, usedHeight, rootBox, new FlowRootBoxPainter(this));

    return wrapperFragment;
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
      case LineBoxFragment lineBoxFragment -> {
        LayoutFragment child = lineBoxFragment.fragments();
        while (child != null) {
          recursePositionLayers(
            layerX + child.borderX(),
            layerY + child.borderY(),
            child, refContext);
          child = child.next();
        }
      }
      case ManagedBoxFragment boxFragment -> {
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
      case UnmanagedBoxFragment boxFragment -> {
        if (boxFragment.box().stackingContext() != refContext) {
          refContext = boxFragment.box().stackingContext();
          refContext.addFragment(layerX, layerY, boxFragment);
        }
        boxFragment.box().content().positionLayers(layerX, layerY);
      }
      default -> throw new UnsupportedOperationException("Don't recognize fragment type!");
    }
  }

  // For testing
  public ManagedBoxFragment rootFragment() {
    return this.rootFragment;
  }

}
