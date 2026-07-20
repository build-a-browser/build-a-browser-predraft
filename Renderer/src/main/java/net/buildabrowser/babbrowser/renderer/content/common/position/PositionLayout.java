package net.buildabrowser.babbrowser.renderer.content.common.position;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    ElementBox box
  ) {
    PosRefBoxFragment refFragment = new PosRefBoxFragment(box);
    box.updatePositioningFragment(refFragment);
    return refFragment;
  }

  public static UnmanagedBoxFragment<?> actuallyLayoutAbsolute(
    ElementBox refBox, float[] insets
  ) {
    LayoutConstraint usedWidthConstraint = LayoutConstraint.of(insets[5]);
    LayoutConstraint usedHeightConstraint = LayoutConstraint.of(insets[4]);

    UnmanagedBoxFragment<?> itemFragment = refBox.layout(
      usedWidthConstraint, usedHeightConstraint);

    float[] margins = refBox.dimensions().getComputedMargin();
    itemFragment.setPos(margins[2], margins[0]);

    return itemFragment;
  }

  public static float[] positionAbsolute(
    float[] insets,
    UnmanagedBoxFragment<?> computedFragment
  ) {
    float leftPos = insets[2];
    float topPos = insets[0];

    float[] margin = computedFragment.box().dimensions().getComputedMargin();
    return new float[] {
      leftPos + margin[2],
      topPos + margin[0]
    };
  }

}
