package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.content.table.TableContent;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;

public final class FlowWidthUtil {
  
  private FlowWidthUtil() {}

  // TODO: Account for items with an intrisic size, width/height constraints auto, and a min/max constraint
  public static LayoutConstraint determineBlockReplacedWidthAndMargins(
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    ElementBox childBox
  ) {
    // TODO: computeIntrinsics is not great to call here, but it's usually not called until
    // the child is being layed out (too late)
    childBox.content().computeIntrinsics(childBox);
    computeHorizontalMarginsOrZero(parentWidthConstraint, childBox);
    LayoutConstraint baseWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      parentWidthConstraint, childBox);
    LayoutConstraint baseHeight = SizingHeightUtil.evaluateAdjustedHeightSize(
      parentHeightConstraint, childBox);
    boolean isHeightAuto = !baseHeight.isBounded();
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return SizingWidthUtil.clampWidth(parentWidthConstraint, childBox, baseWidth);
    }

    if (parentWidthConstraint.isPreLayoutConstraint()) {
      return parentWidthConstraint;
    }

    ElementBoxDimensions boxDimensions = childBox.dimensions();

    LayoutConstraint chosenConstraint = null;
    if (
      isHeightAuto
      && boxDimensions.intrinsicWidth() != -1
    ) {
      chosenConstraint = LayoutConstraint.of(boxDimensions.intrinsicWidth());
    } else if (
      boxDimensions.intrinsicRatio() != -1
      && (
        boxDimensions.intrinsicHeight() != -1
        || !isHeightAuto
    )) { // TODO: Also consider specified height
      float usedHeight = LayoutUtil.constraintOrDim(
        baseHeight, boxDimensions.intrinsicHeight());
      float usedWidth = (int) (usedHeight * boxDimensions.intrinsicRatio());
      chosenConstraint = LayoutConstraint.of(usedWidth);
    } else if (boxDimensions.intrinsicRatio() != -1) {
      // TODO: Compute as for block non-replaced
      chosenConstraint = LayoutConstraint.of(
        EBDimensionsUtil.preferredWidthConstraint(childBox));
    } else if (boxDimensions.intrinsicWidth() != -1) {
      chosenConstraint = LayoutConstraint.of(boxDimensions.intrinsicWidth());
    } else {
      // TODO: Check if window smaller than 300px
      chosenConstraint = LayoutConstraint.of(300);
    }

    return SizingWidthUtil.clampWidth(parentWidthConstraint, childBox, chosenConstraint);
  }

  public static LayoutConstraint evaluateNonReplacedBlockWidthAndMargins(
    LayoutConstraint parentConstraint, ElementBox childBox,
    float extraLeftMargin, float extraRightMargin
  ) {
    LayoutConstraint determinedConstraint = SizingWidthUtil.evaluateAdjustedWidthSize(
      parentConstraint, childBox);
    LayoutConstraint marginLeftConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentConstraint,
      childBox.properties().get(CSSProperty.MARGIN_LEFT));
    LayoutConstraint marginRightConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentConstraint,
      childBox.properties().get(CSSProperty.MARGIN_RIGHT));

    boolean isLeftMarginSet = marginLeftConstraint.isBounded();
    boolean isRightMarginSet = marginRightConstraint.isBounded();
    float usedLeftMargin = isLeftMarginSet ? marginLeftConstraint.value() : 0;
    usedLeftMargin = Math.max(usedLeftMargin, extraLeftMargin);
    float usedRightMargin = isRightMarginSet ? marginRightConstraint.value() : 0;
    usedRightMargin = Math.max(usedRightMargin, extraRightMargin);

    ElementBoxDimensions boxDimensions = childBox.dimensions();

    if (determinedConstraint.isPreLayoutConstraint()) {
      EBDimensionsUtil.setComputedHorizontalMargin(childBox, usedLeftMargin, usedRightMargin);
      return determinedConstraint;
    }

    if (!parentConstraint.isBounded()) {
      EBDimensionsUtil.setComputedHorizontalMargin(childBox, usedLeftMargin, usedRightMargin);
      LayoutConstraint usedConstraint = determinedConstraint.type().equals(LayoutConstraintType.AUTO) ?
        parentConstraint : determinedConstraint;
      return SizingWidthUtil.clampWidth(parentConstraint, childBox, usedConstraint);
    }    

    float[] border = boxDimensions.getComputedBorder();
    float[] padding = boxDimensions.getComputedPadding();

    float autoWidth = parentConstraint.value()
      - usedLeftMargin - usedRightMargin
      - border[2] - border[3] - padding[2] - padding[3];
    
    // TODO: I don't really like this special case
    if (childBox.content() instanceof TableContent) {
      float minWidth = EBDimensionsUtil.preferredMinWidthConstraint(childBox);
      float preferredWidth = EBDimensionsUtil.preferredWidthConstraint(childBox);
      autoWidth = Math.max(Math.min(preferredWidth, autoWidth), minWidth);
    }

    float preclampWidth = Math.max(0,
      determinedConstraint.isBounded() ?
        determinedConstraint.value() : autoWidth);

    LayoutConstraint clampedWidth = SizingWidthUtil.clampWidth(
      parentConstraint, childBox, LayoutConstraint.of(preclampWidth));
    float adjustedWidth = clampedWidth.value();
    
    if (isLeftMarginSet) {
      // Covers both overconstrained and both auto cases
      // TODO: Change once RTL support is added
      usedRightMargin = parentConstraint.value()
        - usedLeftMargin - adjustedWidth
        - border[2] - border[3] - padding[2] - padding[3];
    } else if (isRightMarginSet) {
      usedLeftMargin = parentConstraint.value()
        - usedRightMargin - adjustedWidth
        - border[2] - border[3] - padding[2] - padding[3];
    } else {
      float remainingSpace = parentConstraint.value()
        - adjustedWidth
        - border[2] - border[3] - padding[2] - padding[3];
      usedLeftMargin = remainingSpace / 2;
      usedRightMargin = remainingSpace - usedLeftMargin; // Account for int truncation
    }

    EBDimensionsUtil.setComputedHorizontalMargin(childBox, usedLeftMargin, usedRightMargin);
    return clampedWidth;
  }

  public static LayoutConstraint determineInlineBlockNonReplacedWidthAndMargins(
    LayoutConstraint parentConstraint, ElementBox childBox
  ) {
    computeHorizontalMarginsOrZero(parentConstraint, childBox);
    LayoutConstraint baseWidth = SizingWidthUtil.evaluateAdjustedWidthSize(parentConstraint, childBox);
    
    if (baseWidth.isBounded()) {
      return SizingWidthUtil.clampWidth(parentConstraint, childBox, baseWidth);
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    float preferredMinWidth = EBDimensionsUtil.preferredMinWidthConstraint(childBox);
    float preferredWidth = EBDimensionsUtil.preferredWidthConstraint(childBox);
    float availableWidth = parentConstraint.value();

    LayoutConstraint usedConstraint = !parentConstraint.isBounded() ?
      LayoutConstraint.of(preferredWidth) :
      LayoutConstraint.of(Math.min(Math.max(preferredMinWidth, availableWidth), preferredWidth));

    return SizingWidthUtil.clampWidth(parentConstraint, childBox, usedConstraint);
  }

  public static LayoutConstraint determineFloatNonReplacedWidthAndMargins(
    LayoutConstraint parentConstraint, ElementBox childBox
  ) {
    computeHorizontalMarginsOrZero(parentConstraint, childBox);

    LayoutConstraint baseWidth = SizingWidthUtil.evaluateAdjustedWidthSize(parentConstraint, childBox);
    
    if (baseWidth.isBounded()) {
      return SizingWidthUtil.clampWidth(parentConstraint, childBox, baseWidth);
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    LayoutConstraint usedConstraint = LayoutConstraint.of(Math.min(
      // TODO: Account for margins
      Math.max(
        EBDimensionsUtil.preferredMinWidthConstraint(childBox),
        parentConstraint.value()),
      EBDimensionsUtil.preferredWidthConstraint(childBox)));
    return SizingWidthUtil.clampWidth(parentConstraint, childBox, usedConstraint);
  }

  public static void computeHorizontalMarginsOrZero(
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    LayoutConstraint marginLeftConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentConstraint,
      childBox.properties().get(CSSProperty.MARGIN_LEFT));
    LayoutConstraint marginRightConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentConstraint,
      childBox.properties().get(CSSProperty.MARGIN_RIGHT));

    boolean isLeftMarginSet = marginLeftConstraint.isBounded();
    boolean isRightMarginSet = marginRightConstraint.isBounded();
    float usedLeftMargin = isLeftMarginSet ? marginLeftConstraint.value() : 0;
    float usedRightMargin = isRightMarginSet ? marginRightConstraint.value() : 0;
    EBDimensionsUtil.setComputedHorizontalMargin(childBox, usedLeftMargin, usedRightMargin);
  }

}
