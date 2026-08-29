package net.buildabrowser.babbrowser.renderer.content.common;

import static net.buildabrowser.babbrowser.renderer.content.common.SizingUtil.adjustConstraint;
import static net.buildabrowser.babbrowser.renderer.content.common.SizingUtil.evaluateBaseSizeRaw;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class SizingHeightUtil {
  
  private SizingHeightUtil() {}

  public static LayoutConstraint evaluateAdjustedHeightSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox
  ) {
    return evaluateAdjustedHeightSize(
      parentConstraint, refBox, CSSProperty.HEIGHT,
      refBox.properties().get(CSSProperty.HEIGHT));
  }

  public static LayoutConstraint evaluateAdjustedHeightSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSProperty refProperty,
    CSSValue sizeValue
  ) {
    LayoutConstraint usedParentConstraint = adjustConstraint(
      parentConstraint, refBox, refProperty);
    CalcEvaluation calcResult = CalcInterpreter.evaluateNode(sizeValue,
      innerSizeValue -> evaluateAdjustedHeightSizeRaw(usedParentConstraint, refBox, innerSizeValue));
    LayoutConstraint result = calcResult.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE) ?
      LayoutConstraint.of(calcResult.floatValue()) :
      LayoutConstraint.AUTO;
    return subtractDecor(refBox, result);
  }

  private static LayoutConstraint evaluateBaseHeightSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue
  ) {
    if (
      sizeValue.equals(SizeValue.MIN_CONTENT)
      || sizeValue.equals(SizeValue.MAX_CONTENT)
      || sizeValue.equals(SizeValue.STRETCH)
      || sizeValue.equals(SizeValue.FIT_CONTENT)
      || sizeValue.equals(SizeValue.CONTAIN)
      || sizeValue instanceof SizeValue.FitContent
    ) {
      return LayoutConstraint.AUTO;
    } else {
      return evaluateBaseSizeRaw(layoutContext, parentConstraint, sizeValue);
    }
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

    return constraint;
  }

  public static LayoutConstraint clampHeight(
    LayoutConstraint parentConstraint, ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;

    float adjustedConstraint = constraint.value();

    LayoutConstraint maxConstraint = evaluateAdjustedHeightSize(
      parentConstraint, refBox, CSSProperty.MAX_HEIGHT,
      refBox.properties().get(CSSProperty.MAX_HEIGHT));
    if (maxConstraint.isBounded()) {
      adjustedConstraint = Math.min(adjustedConstraint, maxConstraint.value());
    }

    LayoutConstraint minConstraint = evaluateAdjustedHeightSize(
      parentConstraint, refBox, CSSProperty.MIN_HEIGHT,
      refBox.properties().get(CSSProperty.MIN_HEIGHT));

    if (minConstraint.isBounded()) {
      adjustedConstraint = Math.max(adjustedConstraint, minConstraint.value());
    }

    return LayoutConstraint.of(adjustedConstraint);
  }

  private static LayoutConstraint subtractDecor(
    ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;
    CSSValue boxSizing = refBox.properties().get(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;

    float adjustedConstraint = Math.max(0,
      constraint.value() - refBox.dimensions().decorHeight());
    return LayoutConstraint.of(adjustedConstraint);
  }

}
