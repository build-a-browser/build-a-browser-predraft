package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.SizeStretchingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizeStretchingUtil.SizeStretchResult;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.content.table.TableContent;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public final class FlowWidthUtil {
  
  private FlowWidthUtil() {}

  public static LayoutConstraint determineBlockReplacedWidthAndMargins(
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    ElementBox childBox
  ) {
    // TODO: computeIntrinsics is not great to call here, but it's usually not called until
    // the child is being layed out (too late)
    childBox.content().computeIntrinsics(childBox);
    computeHorizontalMarginsOrZero(parentWidthConstraint, childBox);
    LayoutConstraint baseWidth = SizingWidthUtil.evaluateWidthSize(
      parentWidthConstraint, childBox);
    // Because the height may be clamped, and intrinsic ratios would affect width
    LayoutConstraint baseHeight = FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
      parentHeightConstraint, parentWidthConstraint, LayoutConstraint.AUTO, childBox);
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
    SizeStretchResult stretchData = SizeStretchingUtil.stretch(
      parentConstraint, childBox, extraLeftMargin, extraRightMargin);
    LayoutConstraint stretchConstraint = stretchData.stretchConstraint();
    LayoutConstraint determinedConstraint = SizingWidthUtil.evaluateWidthSize(
      parentConstraint, stretchConstraint, childBox);

    if (determinedConstraint.isPreLayoutConstraint()) {
      EBDimensionsUtil.setComputedHorizontalMargin(childBox,
        stretchData.computedStartMargin(), stretchData.computedEndMargin());
      return determinedConstraint;
    }

    if (!parentConstraint.isBounded()) {
      EBDimensionsUtil.setComputedHorizontalMargin(childBox,
        stretchData.computedStartMargin(), stretchData.computedEndMargin());
      LayoutConstraint usedConstraint = determinedConstraint.type().equals(LayoutConstraintType.AUTO) ?
        parentConstraint : determinedConstraint;
      return SizingWidthUtil.clampWidth(
        parentConstraint, stretchConstraint, childBox, usedConstraint);
    }
    
    float autoWidth = stretchData.stretchConstraint().floatValue();
    // TODO: I don't really like this special case
    if (childBox.content() instanceof TableContent) {
      float minWidth = EBDimensionsUtil.preferredMinWidthConstraint(childBox);
      float preferredWidth = EBDimensionsUtil.preferredWidthConstraint(childBox);
      autoWidth = Math.max(Math.min(preferredWidth, autoWidth), minWidth);
    }

    float preclampStretchedWidth = Math.max(0,
      determinedConstraint.isBounded() ?
        determinedConstraint.value() : autoWidth);

    LayoutConstraint clampedWidth = SizingWidthUtil.clampWidth(
      parentConstraint, stretchConstraint, childBox, LayoutConstraint.of(preclampStretchedWidth));
    float adjustedWidth = clampedWidth.value();
    
    computeNonReplacedBlockMargins(
      childBox, parentConstraint, stretchData, adjustedWidth);
    return clampedWidth;
  }

  private static void computeNonReplacedBlockMargins(
    ElementBox childBox,
    LayoutConstraint parentConstraint,
    SizeStretchResult stretchResult,
    float adjustedWidth
  ) {
    float usedStartMargin = stretchResult.computedStartMargin();
    float usedEndMargin = stretchResult.computedEndMargin();

    float decoredSize = adjustedWidth + stretchResult.decorSize(Measurement.BORDER);
    if (stretchResult.isStartMarginSet()) {
      // Covers both overconstrained and both auto cases
      // TODO: Change once RTL support is added
      usedEndMargin = parentConstraint.value() - usedStartMargin - decoredSize;
    } else if (stretchResult.isEndMarginSet()) {
      usedStartMargin = parentConstraint.value() - usedEndMargin - decoredSize;
    } else {
      float remainingSpace = parentConstraint.value() - decoredSize;
      usedStartMargin = remainingSpace / 2;
      usedEndMargin = remainingSpace - usedStartMargin; // Account for int truncation
    }

    EBDimensionsUtil.setComputedHorizontalMargin(childBox, usedStartMargin, usedEndMargin);
  }

  public static LayoutConstraint determineInlineBlockNonReplacedWidthAndMargins(
    LayoutConstraint parentConstraint, ElementBox childBox
  ) {
    computeHorizontalMarginsOrZero(parentConstraint, childBox);
    LayoutConstraint baseWidth = SizingWidthUtil.evaluateWidthSize(parentConstraint, childBox);
    
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

    LayoutConstraint baseWidth = SizingWidthUtil.evaluateWidthSize(parentConstraint, childBox);
    
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
