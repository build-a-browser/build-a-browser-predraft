package net.buildabrowser.babbrowser.browser.render.content.common;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
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
    CalcEvaluation calcResult = CalcInterpreter.evaluateNode(sizeValue,
      innerSizeValue -> evaluateBaseSizeRaw(layoutContext, parentConstraint, innerSizeValue));
    return calcResult.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE) ?
      LayoutConstraint.of(calcResult.floatValue()) :
      LayoutConstraint.AUTO;
  }

  private static LayoutConstraint evaluateBaseSizeRaw(
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
      return LayoutConstraint.of(percentageValue.value() * parentConstraint.value() / 100);
    } else {
      return LayoutConstraint.AUTO;
    }
  }

  private static LayoutConstraint evaluateBaseWidthSize(
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
      LayoutConstraint innerConstraint = evaluateBaseSizeRaw(layoutContext, parentConstraint, fitContent.optimal());
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
      return evaluateBaseSizeRaw(layoutContext, parentConstraint, sizeValue);
    }

    if (sizeValue.equals(SizeValue.MIN_CONTENT)) {
      return LayoutConstraint.of(referenceDimensions.preferredMinWidthConstraint());
    } else if (sizeValue.equals(SizeValue.MAX_CONTENT)) {
      return LayoutConstraint.of(referenceDimensions.preferredWidthConstraint());
    } else if (sizeValue instanceof SizeValue.FitContent fitContent) {
      LayoutConstraint innerConstraint = evaluateBaseSizeRaw(layoutContext, parentConstraint, fitContent.optimal());
      assert innerConstraint.isBounded();
      float min = referenceDimensions.preferredMinWidthConstraint();
      float max = referenceDimensions.preferredWidthConstraint();
      float preferred = innerConstraint.value();
      return LayoutConstraint.of(Math.clamp(preferred, min, max));
    } else {
      return evaluateBaseSizeRaw(layoutContext, parentConstraint, sizeValue);
    }
  }

  private static LayoutConstraint evaluateBaseHeightSize(
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
      return evaluateBaseSizeRaw(layoutContext, parentConstraint, sizeValue);
    }
  }

  public static LayoutConstraint evaluateAdjustedWidthSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox
  ) {
    return evaluateAdjustedWidthSize(
      parentConstraint, refBox,
      refBox.activeStyles().getProperty(CSSProperty.WIDTH));
  }

  public static LayoutConstraint evaluateAdjustedWidthSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    CalcEvaluation calcResult = CalcInterpreter.evaluateNode(sizeValue,
      innerSizeValue -> evaluateAdjustedWidthSizeRaw(parentConstraint, refBox, innerSizeValue));
    return calcResult.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE) ?
      LayoutConstraint.of(calcResult.floatValue()) :
      LayoutConstraint.AUTO;
  }

  private static LayoutConstraint evaluateAdjustedWidthSizeRaw(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    LayoutConstraint constraint = evaluateBaseWidthSize(
      refBox.layoutContext(), parentConstraint, refBox.dimensions(), sizeValue);
    if (!constraint.isBounded()) return constraint;
    if (constraint.value() < 0) return LayoutConstraint.of(0);

    CSSValue boxSizing = refBox.activeStyles().getProperty(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;

    float adjustedConstraint = Math.max(0,
      constraint.value() - refBox.dimensions().decorWidth());
    return LayoutConstraint.of(adjustedConstraint);
  }

  public static LayoutConstraint evaluateAdjustedHeightSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox
  ) {
    return evaluateAdjustedHeightSize(
      parentConstraint, refBox,
      refBox.activeStyles().getProperty(CSSProperty.HEIGHT));
  }

  public static LayoutConstraint evaluateAdjustedHeightSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    CalcEvaluation calcResult = CalcInterpreter.evaluateNode(sizeValue,
      innerSizeValue -> evaluateAdjustedHeightSizeRaw(parentConstraint, refBox, innerSizeValue));
    return calcResult.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE) ?
      LayoutConstraint.of(calcResult.floatValue()) :
      LayoutConstraint.AUTO;
  }

  private static LayoutConstraint evaluateAdjustedHeightSizeRaw(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    LayoutConstraint constraint = evaluateBaseHeightSize(
      refBox.layoutContext(), parentConstraint, sizeValue);
    if (!constraint.isBounded()) return constraint;
    if (constraint.value() < 0) return LayoutConstraint.of(0);

    CSSValue boxSizing = refBox.activeStyles().getProperty(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;
    assert boxSizing.equals(BoxSizingValue.BORDER_BOX);

    float adjustedConstraint = Math.max(0,
      constraint.value() - refBox.dimensions().decorHeight());
    return LayoutConstraint.of(adjustedConstraint);
  }

  public static LayoutConstraint clampWidth(
    LayoutConstraint parentConstraint, ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;

    float adjustedConstraint = constraint.value();

    LayoutConstraint maxConstraint = evaluateAdjustedWidthSize(
      parentConstraint, refBox,
      refBox.activeStyles().getProperty(CSSProperty.MAX_WIDTH));
    if (maxConstraint.isBounded()) {
      adjustedConstraint = Math.min(adjustedConstraint, maxConstraint.value());
    }

    LayoutConstraint minConstraint = evaluateAdjustedWidthSize(
      parentConstraint, refBox,
      refBox.activeStyles().getProperty(CSSProperty.MIN_WIDTH));
    assert minConstraint.isBounded();
    adjustedConstraint = Math.max(adjustedConstraint, minConstraint.value());

    return LayoutConstraint.of(adjustedConstraint);
  }

  public static LayoutConstraint clampHeight(
    LayoutConstraint parentConstraint, ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;

    float adjustedConstraint = constraint.value();

    LayoutConstraint maxConstraint = evaluateAdjustedHeightSize(
      parentConstraint, refBox,
      refBox.activeStyles().getProperty(CSSProperty.MAX_HEIGHT));
    if (maxConstraint.isBounded()) {
      adjustedConstraint = Math.min(adjustedConstraint, maxConstraint.value());
    }

    LayoutConstraint minConstraint = evaluateAdjustedHeightSize(
      parentConstraint, refBox,
      refBox.activeStyles().getProperty(CSSProperty.MIN_HEIGHT));
    assert minConstraint.isBounded();
    adjustedConstraint = Math.max(adjustedConstraint, minConstraint.value());

    return LayoutConstraint.of(adjustedConstraint);
  }

  private static LayoutConstraint evaluateLengthBaseSize(
    LayoutContext layoutContext,
    LengthValue lengthValue
  ) {
    double baseValue = lengthValue.value().doubleValue();
    double sizeResult = baseValue == 0 ? 0 : switch (lengthValue.dimension()) {
      // TODO: Use real values for EM, EX
      case EM -> baseValue * layoutContext.font().metrics().size();
      case EX -> baseValue * layoutContext.font().metrics().xHeight() / 2;
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
