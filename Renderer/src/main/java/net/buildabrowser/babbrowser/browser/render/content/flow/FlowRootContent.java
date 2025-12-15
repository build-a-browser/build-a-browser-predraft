package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class FlowRootContent implements BoxContent {

  private final ElementBox rootBox;

  private final FlowBlockLayout blockLayout;
  private final FlowInlineLayout inlineLayout;

  private ManagedBoxFragment rootFragment;

  public FlowRootContent(ElementBox box) {
    this.rootBox = box;
    this.blockLayout = new FlowBlockLayout(this);
    this.inlineLayout = new FlowInlineLayout(this);
  }

  @Override
  public void prelayout(LayoutContext layoutContext) {
    for (Box child: rootBox.childBoxes()) {
      if (child instanceof ElementBox elementBox) {
        elementBox.content().prelayout(layoutContext);
      }
    }

    ElementBoxDimensions dimensions = rootBox.dimensions();

    blockLayout.reset(rootBox);
    blockLayout.addChildrenToBlock(layoutContext, rootBox, LayoutConstraint.MIN_CONTENT);
    dimensions.setPreferredMinWidthConstraint(blockLayout.close(LayoutConstraint.MIN_CONTENT).width());

    blockLayout.reset(rootBox);
    blockLayout.addChildrenToBlock(layoutContext, rootBox, LayoutConstraint.MAX_CONTENT);
    dimensions.setPreferredWidthConstraint(blockLayout.close(LayoutConstraint.MAX_CONTENT).width());
  }

  @Override
  public void layout(LayoutContext layoutContext, LayoutConstraint layoutConstraint) {
    blockLayout.reset(rootBox);
    blockLayout.addChildrenToBlock(layoutContext, rootBox, layoutConstraint);

    this.rootFragment = blockLayout.close(layoutConstraint);
    rootBox.dimensions().setComputedSize(rootFragment.width(), rootFragment.height());
  }

  @Override
  public void paint(PaintCanvas canvas) {
    FlowRootContentPainter.paintFragment(canvas, rootFragment);
  }

  FlowBlockLayout blockLayout() {
    return this.blockLayout;
  }

  FlowInlineLayout inlineLayout() {
    return this.inlineLayout;
  }

  // For testing
  ManagedBoxFragment rootFragment() {
    return this.rootFragment;
  }

}
