package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.PercentageValue;

public final class FlowWidthUtil {
  
  private FlowWidthUtil() {}

  public static LayoutConstraint evaluateBaseSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue
  ) {
    if (sizeValue instanceof LengthValue lengthValue) {
      return evaluateLengthBaseSize(layoutContext, lengthValue);
    } else if (
      sizeValue instanceof PercentageValue percentageValue
      && parentConstraint.type().equals(LayoutConstraintType.BOUNDED)
    ) {
      // TODO: Is this the right width to compare against?
      return LayoutConstraint.of(percentageValue.value() * parentConstraint.value() / 100);
    } else {
      return LayoutConstraint.AUTO;
    }
  }

  private static LayoutConstraint evaluateLengthBaseSize(
    LayoutContext layoutContext,
    LengthValue lengthValue
  ) {
    double baseValue = lengthValue.value().doubleValue();
    double sizeResult = baseValue == 0 ? 0 : switch (lengthValue.dimension()) {
      // TODO: Use real values for EM, EX
      case EM -> baseValue * layoutContext.fontMetrics().fontHeight();
      case EX -> baseValue * layoutContext.fontMetrics().fontHeight() / 2;
      case IN -> baseValue * 96;
      case CM -> baseValue * 96 / 2.54;
      case MM -> baseValue * 96 / 2.54 / 100;
      case PT -> baseValue / 0.75;
      case PC -> baseValue * 9;
      case PX -> baseValue;
      default -> throw new UnsupportedOperationException("Unknown Unit!");
    };

    return LayoutConstraint.of((int) sizeResult);
  }

  public static LayoutConstraint determineBlockReplacedWidthAndMargins(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    computeHorizontalMarginsOrZero(layoutContext, parentConstraint, childBox);
    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childBox.activeStyles().getProperty(CSSProperty.WIDTH));
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    ElementBoxDimensions boxDimensions = childBox.dimensions();
    if (
      boxDimensions.intrinsicWidth() != -1
      && boxDimensions.getComputedHeight() != -1
    ) {
      return LayoutConstraint.of(boxDimensions.intrinsicWidth());
    } else if (
      boxDimensions.intrinsicRatio() != -1
      && boxDimensions.intrinsicHeight() != -1
    ) { // TODO: Also consider specified height
      int usedHeight = boxDimensions.intrinsicHeight();
      int usedWidth = (int) (usedHeight * boxDimensions.intrinsicRatio());
      return LayoutConstraint.of(usedWidth);
    } else if (boxDimensions.intrinsicRatio() != -1) {
      // TODO: Compute as for block non-replaced
      return LayoutConstraint.of(boxDimensions.preferredWidthConstraint());
    } else if (boxDimensions.intrinsicWidth() != -1) {
      return LayoutConstraint.of(boxDimensions.intrinsicWidth());
    } else {
      // TODO: Check if window smaller than 300px
      return LayoutConstraint.of(300);
    }
  }

  public static LayoutConstraint evaluateNonReplacedBlockWidthAndMargins(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    LayoutConstraint determinedConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childBox.activeStyles().getProperty(CSSProperty.WIDTH));
    LayoutConstraint marginLeftConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childBox.activeStyles().getProperty(CSSProperty.MARGIN_LEFT));
    LayoutConstraint marginRightConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childBox.activeStyles().getProperty(CSSProperty.MARGIN_RIGHT));

    boolean isLeftMarginSet = marginLeftConstraint.type().equals(LayoutConstraintType.BOUNDED);
    boolean isRightMarginSet = marginRightConstraint.type().equals(LayoutConstraintType.BOUNDED);
    int usedLeftMargin = isLeftMarginSet ? marginLeftConstraint.value() : 0;
    int usedRightMargin = isRightMarginSet ? marginRightConstraint.value() : 0;

    if (determinedConstraint.isPreLayoutConstraint()) {
      childBox.dimensions().setComputedHorizontalMargin(usedLeftMargin, usedRightMargin);
      return determinedConstraint;
    }

    if (!parentConstraint.type().equals(LayoutConstraintType.BOUNDED)) {
      childBox.dimensions().setComputedHorizontalMargin(usedLeftMargin, usedRightMargin);
      return determinedConstraint.type().equals(LayoutConstraintType.AUTO) ?
        parentConstraint : determinedConstraint;
    }    

    int[] border = childBox.dimensions().getComputedBorder();
    int[] padding = childBox.dimensions().getComputedPadding();

    int parentMinusSurroundingsWidth = parentConstraint.value()
      - usedLeftMargin - usedRightMargin
      - border[2] - border[3] - padding[2] - padding[3];
    int adjustedWidth = Math.max(0,
      determinedConstraint.type().equals(LayoutConstraintType.BOUNDED) ?
        determinedConstraint.value() : parentMinusSurroundingsWidth);
    
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
      int remainingSpace = parentConstraint.value()
        - adjustedWidth
        - border[2] - border[3] - padding[2] - padding[3];
      usedLeftMargin = remainingSpace / 2;
      usedRightMargin = remainingSpace - usedLeftMargin; // Account for int truncation
    }

    childBox.dimensions().setComputedHorizontalMargin(usedLeftMargin, usedRightMargin);
    return LayoutConstraint.of(adjustedWidth);
  }

  public static LayoutConstraint determineInlineBlockNonReplacedWidthAndMargins(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    computeHorizontalMarginsOrZero(layoutContext, parentConstraint, childBox);
    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childBox.activeStyles().getProperty(CSSProperty.WIDTH));
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    ElementBoxDimensions boxDimensions = childBox.dimensions();
    int preferredMinWidth = boxDimensions.preferredMinWidthConstraint();
    int preferredWidth = boxDimensions.preferredWidthConstraint();
    int availableWidth = parentConstraint.value();
    if (!parentConstraint.type().equals(LayoutConstraintType.BOUNDED)) {
      return LayoutConstraint.of(preferredWidth);
    }

    return LayoutConstraint.of(Math.min(Math.max(preferredMinWidth, availableWidth), preferredWidth));
  }

  public static LayoutConstraint determineFloatNonReplacedWidthAndMargins(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    computeHorizontalMarginsOrZero(layoutContext, parentConstraint, childBox);
    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childBox.activeStyles().getProperty(CSSProperty.WIDTH));
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    ElementBoxDimensions boxDimensions = childBox.dimensions();
    return LayoutConstraint.of(Math.min(
      // TODO: Account for margins
      Math.max(boxDimensions.preferredMinWidthConstraint(), parentConstraint.value()),
      boxDimensions.preferredWidthConstraint()));
  }

  public static void computeHorizontalMarginsOrZero(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox childBox
  ) {
    LayoutConstraint marginLeftConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childBox.activeStyles().getProperty(CSSProperty.MARGIN_LEFT));
    LayoutConstraint marginRightConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint,
      childBox.activeStyles().getProperty(CSSProperty.MARGIN_RIGHT));

    boolean isLeftMarginSet = marginLeftConstraint.type().equals(LayoutConstraintType.BOUNDED);
    boolean isRightMarginSet = marginRightConstraint.type().equals(LayoutConstraintType.BOUNDED);
    int usedLeftMargin = isLeftMarginSet ? marginLeftConstraint.value() : 0;
    int usedRightMargin = isRightMarginSet ? marginRightConstraint.value() : 0;
    childBox.dimensions().setComputedHorizontalMargin(usedLeftMargin, usedRightMargin);
  }

}
