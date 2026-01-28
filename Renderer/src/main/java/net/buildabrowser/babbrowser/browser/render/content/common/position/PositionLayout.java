package net.buildabrowser.babbrowser.browser.render.content.common.position;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    LayoutContext layoutContext,
    ElementBox box
  ) {
    // TODO: Real math
    LayoutConstraint widthConstraint = LayoutConstraint.of(50);
    LayoutConstraint heightConstraint = LayoutConstraint.AUTO;
    box.content().layout(layoutContext, widthConstraint, heightConstraint);

    ElementBoxDimensions dimensions = box.dimensions();
    int width = LayoutUtil.constraintOrDim(widthConstraint, dimensions.getComputedWidth());
    int height = LayoutUtil.constraintOrDim(heightConstraint, dimensions.getComputedHeight());

    PosRefBoxFragment fragment = new PosRefBoxFragment(width, height, box, layoutContext);
    layoutContext.stackingContext().add(fragment);
    return fragment;
  }

}
