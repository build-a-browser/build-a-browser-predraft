package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.composite.LayerUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.FlowRootContentPainter.FlowRootBoxPainter;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;

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
  public void prelayout(LayoutContext layoutContext) {
    // TODO: This might not be an efficient way to prelayout...
    // Since it lays out a lot of stuff that will be skipped in the real run
    for (Box child: rootBox.childBoxes()) {
      if (child instanceof ElementBox elementBox) {
        elementBox.content().prelayout(layoutContext);
      }
    }

    ElementBoxDimensions dimensions = rootBox.dimensions();

    floatTracker.reset();
    blockLayout.reset(rootBox, LayoutConstraint.MIN_CONTENT, LayoutConstraint.AUTO);
    blockLayout.addChildrenToBlock(
      layoutContext, rootBox, LayoutConstraint.MIN_CONTENT, LayoutConstraint.AUTO);
    dimensions.setPreferredMinWidthConstraint(
      blockLayout.close(LayoutConstraint.MIN_CONTENT, LayoutConstraint.AUTO).contentWidth());

    floatTracker.reset();
    blockLayout.reset(rootBox, LayoutConstraint.MAX_CONTENT, LayoutConstraint.AUTO);
    blockLayout.addChildrenToBlock(
      layoutContext, rootBox, LayoutConstraint.MAX_CONTENT, LayoutConstraint.AUTO);
    dimensions.setPreferredWidthConstraint(
      blockLayout.close(LayoutConstraint.MAX_CONTENT, LayoutConstraint.AUTO).contentWidth());
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    floatTracker.reset();

    blockLayout.reset(rootBox, widthConstraint, heightConstraint);
    blockLayout.addChildrenToBlock(layoutContext, rootBox, widthConstraint, heightConstraint);

    this.rootFragment = blockLayout.close(widthConstraint, heightConstraint);
    rootFragment.setPos(0, 0);
    for (LayoutFragment floatFragment: floatTracker().allFloats()) {
      floatFragment.setParent(rootFragment); // Wasn't implicitly set since it's not added to the managed fragment
    }

    int desiredHeight = Math.max(rootFragment.contentHeight(), floatTracker.contentHeight());
    int usedWidth = LayoutUtil.constraintOrDim(widthConstraint, rootFragment.contentWidth());
    int usedHeight = LayoutUtil.constraintOrDim(heightConstraint, desiredHeight);
    
    UnmanagedBoxFragment wrapperFragment = new UnmanagedBoxFragment(usedWidth, usedHeight, rootBox, new FlowRootBoxPainter(this));

    // For a box that does not start a layer, setting the inner box's parent to the outer box has the effect of inheriting layerX
    // and layerY (because the inner pos is (0, 0), the double boxing doesn't have an adverse effect despite the implicit addition).
    // For a box that does start a layer, we skip the link so the inner box's layerPos is (0, 0).
    if (!LayerUtil.startsLayer(wrapperFragment)) {
      rootFragment.setParent(wrapperFragment);
    }

    return wrapperFragment;
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
