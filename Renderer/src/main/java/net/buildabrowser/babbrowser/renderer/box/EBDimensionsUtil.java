package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class EBDimensionsUtil {
  
  private EBDimensionsUtil() {}


  public static float preferredMinWidthConstraint(ElementBox box) {
    return box.layout(LayoutConstraint.MIN_CONTENT, LayoutConstraint.AUTO).width(Measurement.CONTENT);
  }

  public static float preferredWidthConstraint(ElementBox box) {
    return box.layout(LayoutConstraint.MAX_CONTENT, LayoutConstraint.AUTO).width(Measurement.CONTENT);
  }

  public static void setComputedBorder(
    ElementBox childBox,
    float topBorder, float bottomBorder,
    float leftBorder, float rightBorder
  ) {
    boolean skipIfNone =
      topBorder == 0
      && bottomBorder == 0
      && leftBorder == 0
      && rightBorder == 0;
    childBox.alterDimensions(skipIfNone, d -> d.setComputedBorder(
      topBorder, bottomBorder, leftBorder, rightBorder));
  }

  public static void setComputedPadding(
    ElementBox childBox,
    float topPadding, float bottomPadding,
    float leftPadding, float rightPadding
  ) {
    boolean skipIfNone =
      topPadding == 0
      && bottomPadding == 0
      && leftPadding == 0
      && rightPadding == 0;
    childBox.alterDimensions(skipIfNone, d -> d.setComputedPadding(
      topPadding, bottomPadding, leftPadding, rightPadding));
  }

  public static void setComputedVerticalMargin(
    ElementBox childBox,
    float topMargin, float bottomMargin
  ) {
    boolean skipIfNone = topMargin == 0 && bottomMargin == 0;
    childBox.alterDimensions(skipIfNone, d -> d.setComputedVerticalMargin(
      topMargin, bottomMargin));
  }

  public static void setComputedHorizontalMargin(
    ElementBox childBox,
    float leftMargin, float rightMargin
  ) {
    boolean skipIfNone = leftMargin == 0 && rightMargin == 0;
    childBox.alterDimensions(skipIfNone, d -> d.setComputedHorizontalMargin(
      leftMargin, rightMargin));
  }

}
