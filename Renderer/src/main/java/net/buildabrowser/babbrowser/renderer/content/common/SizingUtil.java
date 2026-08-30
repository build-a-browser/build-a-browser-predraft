package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;

public final class SizingUtil {
  
  private SizingUtil() {}

  public static LayoutConstraint evaluateBaseSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue
  ) {
    return evaluateBaseSize(
      layoutContext, parentConstraint, sizeValue,
      true, true);
  }

  public static LayoutConstraint evaluateBaseSize(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue,
    boolean allowLength,
    boolean allowPercentage
  ) {
    CalcEvaluation calcResult = CalcInterpreter.evaluateNode(sizeValue,
      innerSizeValue -> evaluateBaseSizeRaw(
        layoutContext, parentConstraint, innerSizeValue,
        allowLength, allowPercentage));
    return calcResult.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE) ?
      LayoutConstraint.of(calcResult.floatValue()) :
      LayoutConstraint.AUTO;
  }

  static LayoutConstraint evaluateBaseSizeRaw(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue
  ) {
    return evaluateBaseSizeRaw(
      layoutContext, parentConstraint, sizeValue,
      true, true);
  }

  static LayoutConstraint evaluateBaseSizeRaw(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    CSSValue sizeValue,
    boolean allowLength,
    boolean allowPercentage
  ) {
    if (allowLength && sizeValue instanceof LengthValue lengthValue) {
      return evaluateLengthBaseSize(layoutContext, lengthValue);
    } else if (
      allowPercentage
      && sizeValue instanceof PercentageValue percentageValue
      && parentConstraint.isBounded()
    ) {
      return LayoutConstraint.of(percentageValue.value() * parentConstraint.value() / 100);
    } else {
      return LayoutConstraint.AUTO;
    }
  }

  static LayoutConstraint adjustConstraint(
    LayoutConstraint refConstraint,
    ElementBox refBox,
    CSSProperty refProperty
  ) {
    if (!(
      refConstraint.type().equals(LayoutConstraintType.MIN_CONTENT)
    )) return refConstraint;

    if (!refBox.isReplaced()) {
      return refConstraint;
    }
    
    if (!(
      refProperty.equals(CSSProperty.WIDTH)
      || refProperty.equals(CSSProperty.MAX_WIDTH)
      || refProperty.equals(CSSProperty.HEIGHT)
      || refProperty.equals(CSSProperty.MAX_HEIGHT)
    )) return refConstraint;

    return LayoutConstraint.of(0);
  }

  private static LayoutConstraint evaluateLengthBaseSize(
    LayoutContext layoutContext,
    LengthValue lengthValue
  ) {

    if (LengthType.FR.equals(lengthValue.dimension())) {
      return LayoutConstraint.AUTO;
    }

    Viewport viewport = layoutContext.global().viewport();
    double baseValue = lengthValue.value().doubleValue();
    double sizeResult = baseValue == 0 ? 0 : baseValue * switch (lengthValue.dimension()) {
      case EM -> layoutContext.font().metrics().size();
      case REM -> layoutContext.rootMetrics().size();
      case EX -> layoutContext.font().metrics().xHeight() / 2f;
      case CH -> layoutContext.font().metrics().stringWidth("0"); // TODO: Check inline direction

      case CM -> 96f / 2.54f;
      case MM -> 96f / 2.54f / 10f;
      case Q -> 96f / 2.54f / 40f;
      case IN -> 96f;
      case PT -> 1f / 0.75f;
      case PC -> 9f;
      case PX -> 1f;

      case VW -> viewport.width() / 100f;
      case VH -> viewport.height() / 100f;
      case VMIN -> Math.min(viewport.width(), viewport.height()) / 100f;
      case VMAX -> Math.max(viewport.width(), viewport.height()) / 100f;

      default -> throw new UnsupportedOperationException("Unknown Unit!");
    };

    return LayoutConstraint.of((float) sizeResult);
  }

}
