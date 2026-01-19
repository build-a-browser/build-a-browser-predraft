package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.PercentageValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

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

  public static LayoutConstraint determineBlockReplacedWidth(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ActiveStyles childStyles,
    ElementBoxDimensions boxDimensions
  ) {
    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childStyles.getProperty(CSSProperty.WIDTH));
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    if (parentConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

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

}
