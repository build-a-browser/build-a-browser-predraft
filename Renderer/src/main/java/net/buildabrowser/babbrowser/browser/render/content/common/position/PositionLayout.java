package net.buildabrowser.babbrowser.browser.render.content.common.position;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    LayoutContext layoutContext,
    ElementBox box
  ) {
    // TODO: Real math
    LayoutConstraint widthConstraint = LayoutConstraint.of(50);
    LayoutConstraint heightConstraint = LayoutConstraint.AUTO;
    UnmanagedBoxFragment fragment = box.content().layout(layoutContext, widthConstraint, heightConstraint);

    PosRefBoxFragment refFragment = new PosRefBoxFragment(fragment, layoutContext);
    layoutContext.stackingContext().add(refFragment);
    return refFragment;
  }

}
