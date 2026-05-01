package net.buildabrowser.babbrowser.render.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;

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

  static LayoutConstraint evaluateBaseSizeRaw(
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
