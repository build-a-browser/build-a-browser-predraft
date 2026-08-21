package net.buildabrowser.babbrowser.renderer.content.common;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;
import static net.buildabrowser.babbrowser.renderer.content.common.SizingUtil.evaluateBaseSizeRaw;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizeStretchingUtil.SizeStretchResult;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class SizingWidthUtil {
  
  private SizingWidthUtil() {}

  public static LayoutConstraint evaluateWidthSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox
  ) {
    return evaluateWidthSize(
      parentConstraint, refBox,
      refBox.properties().get(CSSProperty.WIDTH));
  }

  public static LayoutConstraint evaluateWidthSize(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    SizeStretchResult stretchData = SizeStretchingUtil.stretch(
      parentConstraint, refBox, 0, 0);
    LayoutConstraint stretchConstraint = stretchData.stretchConstraint();
    return evaluateWidthSize(
      parentConstraint, stretchConstraint, refBox,
      sizeValue);
  }

  public static LayoutConstraint evaluateWidthSize(
    LayoutConstraint parentConstraint,
    LayoutConstraint stretchConstraint,
    ElementBox refBox
  ) {
    return evaluateWidthSize(
      parentConstraint, stretchConstraint, refBox,
      refBox.properties().get(CSSProperty.WIDTH));
  }

  public static LayoutConstraint evaluateWidthSize(
    LayoutConstraint parentConstraint,
    LayoutConstraint stretchConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    CalcEvaluation calcResult = CalcInterpreter.evaluateNode(sizeValue,
      innerSizeValue -> evaluateStretchedWidthSizeRaw(
      parentConstraint, stretchConstraint, refBox, innerSizeValue));
    LayoutConstraint reuslt = calcResult.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE) ?
      LayoutConstraint.of(calcResult.floatValue()) :
      LayoutConstraint.AUTO;
    return subtractDecor(refBox, reuslt);
  }

  public static LayoutConstraint clampWidth(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    LayoutConstraint constraint
  ) {
    SizeStretchResult stretchData = SizeStretchingUtil.stretch(
      parentConstraint, refBox, 0, 0);
    LayoutConstraint stretchConstraint = stretchData.stretchConstraint();

    return clampWidth(parentConstraint, stretchConstraint, refBox, constraint);
  }

  public static LayoutConstraint clampWidth(
    LayoutConstraint parentConstraint,
    LayoutConstraint stretchConstraint,
    ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;

    float adjustedConstraint = constraint.value();

    LayoutConstraint maxConstraint = evaluateWidthSize(
      parentConstraint, stretchConstraint, refBox,
      refBox.properties().get(CSSProperty.MAX_WIDTH));
    if (maxConstraint.isBounded()) {
      adjustedConstraint = Math.min(adjustedConstraint, maxConstraint.value());
    }

    LayoutConstraint minConstraint = evaluateWidthSize(
      parentConstraint, stretchConstraint, refBox,
      refBox.properties().get(CSSProperty.MIN_WIDTH));

    assert minConstraint.isBounded() || !parentConstraint.isBounded();
    if (minConstraint.isBounded()) {
      adjustedConstraint = Math.max(adjustedConstraint, minConstraint.value());
    }

    return LayoutConstraint.of(adjustedConstraint);
  }

  private static LayoutConstraint evaluateStretchedWidthSizeRaw(
    LayoutConstraint parentConstraint,
    LayoutConstraint stretchConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    // Switching on enum values unfortunately incurs a performance penalty here
    // Since sizeValue is a CSSValue, not SizeValue
    // addDecor counteracts that later subtractDecor,
    // since these are treated the same regardless
    if (SizeValue.STRETCH.equals(sizeValue)) {
      return addDecor(refBox, stretchConstraint);
    } else if (SizeValue.FIT_CONTENT.equals(sizeValue)) {
      return addDecor(refBox, parentConstraint.isPreLayoutConstraint() ?
        parentConstraint :
        computeFitContent(stretchConstraint, refBox));
    } else {
      return evaluateAdjustedWidthSizeRaw(
        parentConstraint, refBox, sizeValue);
    }
  }

  private static LayoutConstraint evaluateAdjustedWidthSizeRaw(
    LayoutConstraint parentConstraint,
    ElementBox refBox,
    CSSValue sizeValue
  ) {
    LayoutConstraint constraint = evaluateBaseWidthSize(
      refBox.layoutContext(), parentConstraint, refBox, sizeValue);
    if (!constraint.isBounded()) return constraint;
    if (constraint.value() < 0) return LayoutConstraint.of(0);

    return constraint;
  }

  private static LayoutConstraint evaluateBaseWidthSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ElementBox referenceBox,
    CSSValue sizeValue
  ) {
    // Pre-layout
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
    
    // Normal layout
    } else if (sizeValue.equals(SizeValue.MIN_CONTENT)) {
      return LayoutConstraint.of(EBDimensionsUtil.preferredMinWidthConstraint(referenceBox));
    } else if (sizeValue.equals(SizeValue.MAX_CONTENT)) {
      return LayoutConstraint.of(EBDimensionsUtil.preferredWidthConstraint(referenceBox));
    } else if (sizeValue instanceof SizeValue.FitContent fitContent) {
      LayoutConstraint innerConstraint = evaluateBaseSizeRaw(layoutContext, parentConstraint, fitContent.optimal());
      assert innerConstraint.isBounded();
      float min = EBDimensionsUtil.preferredMinWidthConstraint(referenceBox);
      float max = EBDimensionsUtil.preferredWidthConstraint(referenceBox);
      float preferred = innerConstraint.value();
      return LayoutConstraint.of(mathClamp(preferred, min, max));
    } else {
      return evaluateBaseSizeRaw(layoutContext, parentConstraint, sizeValue);
    }
  }

  private static LayoutConstraint computeFitContent(
    LayoutConstraint stretchConstraint,
    ElementBox refBox
  ) {
    if (!stretchConstraint.isBounded()) return stretchConstraint;
    float stretch = stretchConstraint.value();
    float minContent = EBDimensionsUtil.preferredMinWidthConstraint(refBox);
    float maxContent = EBDimensionsUtil.preferredWidthConstraint(refBox);
    float fit = Math.min(maxContent, Math.max(minContent, stretch));
    return LayoutConstraint.of(fit);
  }

  private static LayoutConstraint subtractDecor(
    ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;
    CSSValue boxSizing = refBox.properties().get(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;

    float adjustedConstraint = Math.max(0,
      constraint.value() - refBox.dimensions().decorWidth());
    return LayoutConstraint.of(adjustedConstraint);
  }

  private static LayoutConstraint addDecor(
    ElementBox refBox,
    LayoutConstraint constraint
  ) {
    if (!constraint.isBounded()) return constraint;
    CSSValue boxSizing = refBox.properties().get(CSSProperty.BOX_SIZING);
    if (boxSizing.equals(BoxSizingValue.CONTENT_BOX)) return constraint;

    float adjustedConstraint = Math.max(0,
      constraint.value() + refBox.dimensions().decorWidth());
    return LayoutConstraint.of(adjustedConstraint);
  }

}
