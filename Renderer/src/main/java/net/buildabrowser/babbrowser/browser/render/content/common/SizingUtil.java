package net.buildabrowser.babbrowser.browser.render.content.common;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;

public final class SizingUtil {
  
  private SizingUtil() {}

  public static LayoutConstraint evaluateBaseSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue
  ) {
    if (sizeValue instanceof LengthValue lengthValue) {
      return evaluateLengthBaseSize(layoutContext, lengthValue);
    } else if (
      sizeValue instanceof PercentageValue percentageValue
      && parentConstraint.isBounded()
    ) {
      // TODO: Is this the right width to compare against?
      return LayoutConstraint.of(percentageValue.value() * parentConstraint.value() / 100);
    } else {
      return LayoutConstraint.AUTO;
    }
  }

  public static LayoutConstraint evaluateBaseWidthSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBoxDimensions referenceDimensions,
    CSSValue sizeValue
  ) {
    // TODO: Is this a good way to handle prelayout?
    if (
      parentConstraint.isPreLayoutConstraint()
      && sizeValue instanceof SizeValue.FitContent fitContent
    ) {
      LayoutConstraint innerConstraint = evaluateBaseSize(layoutContext, parentConstraint, fitContent.optimal());
      if (innerConstraint.isBounded()) {
        return innerConstraint;
      }
      return parentConstraint;
    } else if (
      parentConstraint.type().equals(LayoutConstraintType.MIN_CONTENT)
      && sizeValue.equals(SizeValue.MIN_CONTENT)
    ) {
      return LayoutConstraint.MIN_CONTENT;
    } else if (
      parentConstraint.type().equals(LayoutConstraintType.MAX_CONTENT)
      && sizeValue.equals(SizeValue.MAX_CONTENT)
    ) {
      return LayoutConstraint.MAX_CONTENT;
    } else if (parentConstraint.isPreLayoutConstraint()) {
      return evaluateBaseSize(layoutContext, parentConstraint, sizeValue);
    }

    if (sizeValue.equals(SizeValue.MIN_CONTENT)) {
      return LayoutConstraint.of(referenceDimensions.preferredMinWidthConstraint());
    } else if (sizeValue.equals(SizeValue.MAX_CONTENT)) {
      return LayoutConstraint.of(referenceDimensions.preferredWidthConstraint());
    } else if (sizeValue instanceof SizeValue.FitContent fitContent) {
      LayoutConstraint innerConstraint = evaluateBaseSize(layoutContext, parentConstraint, fitContent.optimal());
      assert innerConstraint.isBounded();
      float min = referenceDimensions.preferredMinWidthConstraint();
      float max = referenceDimensions.preferredWidthConstraint();
      float preferred = innerConstraint.value();
      return LayoutConstraint.of(Math.clamp(preferred, min, max));
    } else {
      return evaluateBaseSize(layoutContext, parentConstraint, sizeValue);
    }
  }

  public static LayoutConstraint evaluateBaseHeightSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue
  ) {
    if (
      sizeValue.equals(SizeValue.MIN_CONTENT)
      || sizeValue.equals(SizeValue.MAX_CONTENT)
      || sizeValue instanceof SizeValue.FitContent
    ) {
      return LayoutConstraint.AUTO;
    } else {
      return evaluateBaseSize(layoutContext, parentConstraint, sizeValue);
    }
  }

  public static LayoutConstraint evaluateAdjustedWidthSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    LayoutConstraint constraint = evaluateBaseWidthSize(
      refBox.layoutContext(), parentConstraint, refBox.dimensions(), sizeValue);
    if (!constraint.isBounded()) return constraint;

    CSSValue boxSizing = refBox.activeStyles().getProperty(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;
    assert boxSizing.equals(BoxSizingValue.BORDER_BOX);

    float adjustedConstraint = Math.max(0,
      constraint.value() - refBox.dimensions().decorWidth());
    return LayoutConstraint.of(adjustedConstraint);
  }

  public static LayoutConstraint evaluateAdjustedHeightSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    LayoutConstraint constraint = evaluateBaseHeightSize(
      refBox.layoutContext(), parentConstraint, sizeValue);
    if (!constraint.isBounded()) return constraint;

    CSSValue boxSizing = refBox.activeStyles().getProperty(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;
    assert boxSizing.equals(BoxSizingValue.BORDER_BOX);

    float adjustedConstraint = Math.max(0,
      constraint.value() - refBox.dimensions().decorHeight());
    return LayoutConstraint.of(adjustedConstraint);
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

    return LayoutConstraint.of((float) sizeResult);
  }

}
