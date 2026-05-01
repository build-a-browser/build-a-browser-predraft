package net.buildabrowser.babbrowser.render.content.common;

import static net.buildabrowser.babbrowser.render.content.common.SizingUtil.evaluateBaseSizeRaw;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;

public final class SizingWidthUtil {
  
  private SizingWidthUtil() {}

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

    assert minConstraint.isBounded() || !parentConstraint.isBounded();
    if (minConstraint.isBounded()) {
      adjustedConstraint = Math.max(adjustedConstraint, minConstraint.value());
    }

    return LayoutConstraint.of(adjustedConstraint);
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

}
