package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;

public final class LayoutUtil {
  
  private LayoutUtil() {}

  public static float constraintOrDim(LayoutConstraint constraint, float dim) {
    return constraint.isBounded() ?
      constraint.value() : dim;
  }

  // In case it wasn't originally resolved. Passing AUTO should be fine because if the parent is
  // definite it should have already resolved anyways. Note that this will not resolve child percentages
  // if the original clamp was not definite, that is intentional.
  // However, we must preserve the prelayout state
  public static float clampedUsedWidth(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    float dim
  ) {
    float preclampWidth = LayoutUtil.constraintOrDim(widthConstraint, dim);
    float usedWidth = SizingWidthUtil.clampWidth(
      widthConstraint.isBounded() ? LayoutConstraint.AUTO : widthConstraint,
      rootBox, LayoutConstraint.of(preclampWidth)).value();
    return usedWidth;
  }

  public static float clampedUsedHeight(
    ElementBox rootBox,
    LayoutConstraint heightConstraint,
    float dim
  ) {
    float preclampHeight = LayoutUtil.constraintOrDim(heightConstraint, dim);
    float usedHeight = SizingHeightUtil.clampHeight(
      heightConstraint.isBounded() ? LayoutConstraint.AUTO : heightConstraint,
      rootBox, LayoutConstraint.of(preclampHeight)).value();
    return usedHeight;
  }

}
