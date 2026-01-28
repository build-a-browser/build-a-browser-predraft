package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.composite.LayerScannerUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

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
  public void layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    floatTracker.reset();

    blockLayout.reset(rootBox, widthConstraint, heightConstraint);
    blockLayout.addChildrenToBlock(layoutContext, rootBox, widthConstraint, heightConstraint);

    this.rootFragment = blockLayout.close(widthConstraint, heightConstraint);
    rootFragment.setPos(0, 0);

    int desiredHeight = Math.max(rootFragment.contentHeight(), floatTracker.contentHeight());
    int usedHeight = LayoutUtil.constraintOrDim(heightConstraint, desiredHeight);
    rootBox.dimensions().setComputedSize(rootFragment.contentWidth(), usedHeight);
  }

  @Override
  public void layer(CompositeLayer layer) {
    for (LayoutFragment floatFragment: floatTracker.allFloats()) {
      if (!LayerScannerUtil.startsLayer(floatFragment)) continue;
      // TODO: Adjust the code below
      // LayerScannerUtil.createLayerForBox(layer, (BoxFragment) floatFragment, new int[2]);
    }
    LayerScannerUtil.scanLayers(layer, rootFragment);
  }

  @Override
  public void paint(PaintCanvas canvas) {
    FlowRootContentPainter.paint(canvas, this, this.rootFragment);
  }

  @Override
  public void paintBackground(PaintCanvas canvas) {
    FlowRootContentPainter.paintBackground(canvas, this, rootFragment);
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
