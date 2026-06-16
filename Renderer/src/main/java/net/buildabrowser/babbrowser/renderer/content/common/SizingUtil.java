package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
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

  private static LayoutConstraint evaluateLengthBaseSize(
    LayoutContext layoutContext,
    LengthValue lengthValue
  ) {
    Viewport viewport = layoutContext.global().viewport();
    double baseValue = lengthValue.value().doubleValue();
    double sizeResult = baseValue == 0 ? 0 : baseValue * switch (lengthValue.dimension()) {
      case EM -> layoutContext.font().metrics().size();
      case REM -> layoutContext.global().rootMetrics().size();
      case EX -> layoutContext.font().metrics().xHeight() / 2;
      case CH -> layoutContext.font().metrics().stringWidth("0"); // TODO: Check inline direction

      case CM -> 96 / 2.54;
      case MM -> 96 / 2.54 / 10;
      case Q -> 96 / 2.54 / 40;
      case IN -> 96;
      case PT -> 1 / 0.75;
      case PC -> 9;
      case PX -> 1;

      case VW -> viewport.width() / 100;
      case VH -> viewport.height() / 100;
      case VMIN -> Math.min(viewport.width(), viewport.height()) / 100;
      case VMAX -> Math.max(viewport.width(), viewport.height()) / 100;

      default -> throw new UnsupportedOperationException("Unknown Unit!");
    };

    return LayoutConstraint.of((float) sizeResult);
  }

}
