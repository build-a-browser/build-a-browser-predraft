package net.buildabrowser.babbrowser.cssbase.property.calc;

import java.util.Iterator;
import java.util.function.Function;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcAngleEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcNumberEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcClampFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncDouble;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncMany;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncSingle;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcKeyword;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcLogFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcNumber;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcRoundFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcType;

public class CalcInterpreter {
 
  // TODO: It would be nice to do constant propogation right after parse
  public static CalcEvaluation evaluateNode(
    CSSValue node, Function<CSSValue, CalcEvaluation> fallbackCalc
  ) {
    if (node instanceof CalcValue calcValue) {
      return evaluateMathNode(calcValue, fallbackCalc);
    } else if (node instanceof CalcNumber calcNumber) {
      return CalcNumberEvaluation.create(calcNumber.value(), calcNumber.isInteger());
    } else if (node instanceof CalcKeyword keyword) {
      return CalcNumberEvaluation.create(keyword.value(), false);
    } else {
      return fallbackCalc.apply(node);
    }
  }

  private static CalcEvaluation evaluateMathNode(
    CalcValue calcValue, Function<CSSValue, CalcEvaluation> fallbackCalc
  ) {
    return switch (calcValue.type()) {
      case CALC -> evaluateNode(((CalcFuncSingle) calcValue).arg(), fallbackCalc);
      case ADD, SUB -> evaluateAddSub((CalcFuncDouble) calcValue, fallbackCalc);
      case MUL, DIV -> evaluateMulDiv((CalcFuncDouble) calcValue, fallbackCalc);
      case MIN, MAX -> evaluateMinMax((CalcFuncMany) calcValue, fallbackCalc);
      case CLAMP -> evaluateClamp((CalcClampFunc) calcValue, fallbackCalc);
      case ROUND -> evaluateRound((CalcRoundFunc) calcValue, fallbackCalc);
      case MOD, REM -> evaluateModRem((CalcFuncDouble) calcValue, fallbackCalc);
      case SIN, COS, TAN -> evaluateSingleArgTrigFunction(calcValue, fallbackCalc);
      case ASIN, ACOS, ATAN -> evaluateSingleArgATrigFunction(calcValue, fallbackCalc);
      case ATAN2 -> evaluateAtan2((CalcFuncDouble) calcValue, fallbackCalc);
      case POW -> evaluatePow((CalcFuncDouble) calcValue, fallbackCalc);
      case HYPOT -> evaluateHypot((CalcFuncMany) calcValue, fallbackCalc);
      case LOG -> evaluateLog((CalcLogFunc) calcValue, fallbackCalc);
      case SQRT, EXP, ABS, SIGN -> evaluateSingleArgMathFunc((CalcFuncSingle) calcValue, fallbackCalc);
      default -> throw new UnsupportedOperationException("Unrecognized calc type: " + calcValue.type());
    };
  }

  // TODO: The spec is less restrictive about types than these functions, adjust it later

  private static CalcEvaluation evaluateAddSub(
    CalcFuncDouble calcValue, Function<CSSValue, CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation leftEval = evaluateNode(calcValue.leftArg(), fallbackCalc);
    CalcEvaluation rightEval = evaluateNode(calcValue.rightArg(), fallbackCalc);
    if (
      leftEval.isFailure()
      || leftEval.valueType() != rightEval.valueType()
    ) return CalcEvaluation.FAILURE;

    float result = calcValue.type().equals(CalcType.ADD) ?
      leftEval.floatValue() + rightEval.floatValue() :
      leftEval.floatValue() - rightEval.floatValue();
    return leftEval.derive(result);
  }

  private static CalcEvaluation evaluateMulDiv(
    CalcFuncDouble calcValue, Function<CSSValue, CalcEvaluation> fallbackCalc
  ) {
    boolean isMul = calcValue.type().equals(CalcType.MUL);
    CalcEvaluation leftEval = evaluateNode(calcValue.leftArg(), fallbackCalc);
    CalcEvaluation rightEval = evaluateNode(calcValue.rightArg(), fallbackCalc);
    if (
      (leftEval.isFailure() || rightEval.isFailure())
      || (!isMul && !rightEval.isNumber())
      || !(
        leftEval.sameType(rightEval)
        || leftEval.isNumber()
        || rightEval.isNumber()
      )
    ) {
      return CalcEvaluation.FAILURE;
    }

    float result = isMul ?
      leftEval.floatValue() * rightEval.floatValue() :
      leftEval.floatValue() / rightEval.floatValue();
    return leftEval.isNumber() ?
      rightEval.derive(result) :
      leftEval.derive(result);
  }

  private static CalcEvaluation evaluateMinMax(
    CalcFuncMany calcValue, Function<CSSValue, CalcEvaluation> fallbackCalc
  ) {
    if (calcValue.args().isEmpty()) return CalcEvaluation.FAILURE;

    CalcEvaluation firstEval = evaluateNode(calcValue.args().get(0), fallbackCalc);
    if (firstEval.isFailure()) return firstEval;

    boolean isMin = calcValue.type().equals(CalcType.MIN);
    float result = firstEval.floatValue();
    Iterator<CSSValue> argIt = calcValue.args().listIterator(1);
    while (argIt.hasNext()) {
      CalcEvaluation nextEval = evaluateNode(argIt.next(), fallbackCalc);
      if (
        nextEval.isFailure()
        || !firstEval.sameType(nextEval)
      ) return CalcEvaluation.FAILURE;
      
      float nextValue = nextEval.floatValue();
      result = isMin ? Math.min(result, nextValue) : Math.max(result, nextValue);
    }

    return firstEval.derive(result);
  }

  private static CalcEvaluation evaluateClamp(
    CalcClampFunc calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation minEval = calcValue.minValue().equals(CSSValue.NONE) ?
      null :
      evaluateNode(calcValue.minValue(), fallbackCalc);
    if (minEval != null && minEval.isFailure()) return minEval;

    CalcEvaluation idealEval = evaluateNode(calcValue.idealValue(), fallbackCalc);
    if (idealEval.isFailure()) return idealEval;

    CalcEvaluation maxEval = calcValue.maxValue().equals(CSSValue.NONE) ?
      null :
      evaluateNode(calcValue.maxValue(), fallbackCalc);
    if (maxEval != null && maxEval.isFailure()) return maxEval;

    if (
      (minEval != null && !minEval.sameType(idealEval))
      || (maxEval != null && !maxEval.sameType(idealEval))
    ) return CalcEvaluation.FAILURE;

    float result = idealEval.floatValue();
    if (minEval != null) {
      result = Math.max(result, minEval.floatValue());
    }
    if (maxEval != null) {
      result = Math.min(result, maxEval.floatValue());
    }

    return idealEval.derive(result);
  }

  private static CalcEvaluation evaluateRound(CalcRoundFunc calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc) {
    CalcEvaluation aEval = evaluateNode(calcValue.a(), fallbackCalc);
    if (aEval.isFailure()) return aEval;

    boolean isLineWidth = calcValue.roundingStrategy().equals(CalcRoundFunc.RoundingStrategy.LINE_WIDTH);
    CalcEvaluation bEval = null;
    if (calcValue.b() != null) {
      bEval = evaluateNode(calcValue.b(), fallbackCalc);
      if (bEval.isFailure()) return bEval;
    } else if (aEval.isNumber() || isLineWidth) {
      bEval = aEval.derive(1);
    } else {
      return CalcEvaluation.FAILURE;
    }

    if (
      (!isLineWidth && !aEval.sameType(bEval))
      || (isLineWidth && !bEval.isNumber())
      || (isLineWidth && !aEval.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE))
    ) return CalcEvaluation.FAILURE;

    if (
      bEval.floatValue() == 0
      || Float.isInfinite(aEval.floatValue()) && Float.isInfinite(bEval.floatValue())
    ) {
      return aEval.derive(Float.NaN);
    } else if (Float.isInfinite(aEval.floatValue())) {
      return aEval;
    } else if (!Float.isInfinite(aEval.floatValue())) {
      // Hopefully this preserves to 0 type
      float argAsZero =
        aEval.floatValue() == 0 ? aEval.floatValue() :
        aEval.floatValue() > 0 ? 0 : -0;
      return switch (calcValue.roundingStrategy()) {
        case NEAREST, LINE_WIDTH, TO_ZERO -> aEval.derive(argAsZero);
        case UP -> aEval.derive(aEval.floatValue() > 0 ? Float.POSITIVE_INFINITY : argAsZero);
        case DOWN -> aEval.derive(aEval.floatValue() < 0 ? Float.NEGATIVE_INFINITY : argAsZero);
        default -> throw new UnsupportedOperationException("Unsupported Rounding Strategy: " + calcValue.roundingStrategy());
      };
    }

    float lowerB = (float) Math.floor(aEval.floatValue() / bEval.floatValue()) * bEval.floatValue();
    float upperB = lowerB + bEval.floatValue();
    boolean isCloserToLower = (aEval.floatValue() - lowerB) < (upperB - aEval.floatValue());
    float chosenValue = switch (calcValue.roundingStrategy()) {
      case NEAREST -> isCloserToLower ? lowerB : upperB;
      case UP -> upperB;
      case DOWN -> lowerB;
      case TO_ZERO -> aEval.floatValue() >= 0 ? lowerB : upperB;
      // TODO: Snap to line width in some cases
      case LINE_WIDTH -> isCloserToLower ? lowerB : upperB;
      default -> throw new UnsupportedOperationException("Unsupported Rounding Strategy: " + calcValue.roundingStrategy());
    };

    return aEval.derive(chosenValue);
  }

  private static CalcEvaluation evaluateModRem(
    CalcFuncDouble calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation leftEval = evaluateNode(calcValue.leftArg(), fallbackCalc);
    CalcEvaluation rightEval = evaluateNode(calcValue.rightArg(), fallbackCalc);
    if (
      leftEval.isFailure() || rightEval.isFailure() 
      || !leftEval.sameType(rightEval)
    ) {
      return CalcEvaluation.FAILURE;
    }

    if (
      rightEval.floatValue() == 0
      || Float.isInfinite(leftEval.floatValue())
    ) return rightEval.derive(Float.NaN);

    if (calcValue.type().equals(CalcType.MOD)) {
      if (
        (rightEval.floatValue() == Float.POSITIVE_INFINITY
        || rightEval.floatValue() == Float.NEGATIVE_INFINITY)
        && Math.signum(leftEval.floatValue()) != Math.signum(rightEval.floatValue())
      ) return rightEval.derive(Float.NaN);

      return leftEval.derive(leftEval.floatValue() % rightEval.floatValue());
    }

    CalcEvaluation roundingResult = evaluateRound(new CalcRoundFunc(
      CalcType.ROUND, CalcRoundFunc.RoundingStrategy.TO_ZERO, 
      calcValue.leftArg(), calcValue.rightArg()
    ), fallbackCalc);
    if (roundingResult.isFailure()) return roundingResult;

    return leftEval.derive(leftEval.floatValue() - roundingResult.floatValue());
  }

  private static CalcEvaluation evaluateSingleArgTrigFunction(
    CalcValue calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation argEval = evaluateNode(((CalcFuncSingle) calcValue).arg(), fallbackCalc);
    if (
      argEval.isFailure()
      || !(argEval.isNumber() || argEval.valueType().equals(CalcEvalType.ANGLE))
    ) return CalcEvaluation.FAILURE;

    double argInRadians = argEval.valueType().equals(CalcEvalType.ANGLE) ?
      Math.toRadians(argEval.floatValue()) :
      argEval.floatValue();
    // It seems these functions handle the checks for us
    double result = switch (calcValue.type()) {
      case SIN -> Math.sin(argInRadians);
      case COS -> Math.cos(argInRadians);
      case TAN -> Math.tan(argInRadians);
      default -> throw new UnsupportedOperationException("Unrecognized trig function: " + calcValue.type());
    };

    return argEval.derive((float) result);
  }

  private static CalcEvaluation evaluateSingleArgATrigFunction(
    CalcValue calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation argEval = evaluateNode(((CalcFuncSingle) calcValue).arg(), fallbackCalc);
    if (
      argEval.isFailure()
      || !argEval.isNumber()
    ) return CalcEvaluation.FAILURE;

    // Again, these functions seem to handle the checks for us
    double result = switch (calcValue.type()) {
      case ASIN -> Math.asin(argEval.floatValue());
      case ACOS -> Math.acos(argEval.floatValue());
      case ATAN -> Math.atan(argEval.floatValue());
      default -> throw new UnsupportedOperationException("Unrecognized inverse trig function: " + calcValue.type());
    };

    return argEval.derive((float) result);
  }

  private static CalcEvaluation evaluateAtan2(
    CalcFuncDouble calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation leftEval = evaluateNode(calcValue.leftArg(), fallbackCalc);
    if (leftEval.isFailure()) return leftEval;
    CalcEvaluation rightEval = evaluateNode(calcValue.rightArg(), fallbackCalc);
    if (rightEval.isFailure()) return rightEval;

    if (
      !leftEval.sameType(rightEval)
      || !(
        leftEval.isNumber()
        || leftEval.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE))
    ) return CalcEvaluation.FAILURE;

    double result = Math.atan2(leftEval.floatValue(), rightEval.floatValue());
    return CalcAngleEvaluation.create((float) result);
  }

  private static CalcEvaluation evaluatePow(
    CalcFuncDouble calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation leftEval = evaluateNode(calcValue.leftArg(), fallbackCalc);
    if (leftEval.isFailure()) return leftEval;
    CalcEvaluation rightEval = evaluateNode(calcValue.rightArg(), fallbackCalc);
    if (rightEval.isFailure()) return rightEval;

    if (
      !rightEval.isNumber()
      || !rightEval.isNumber()
    ) return CalcEvaluation.FAILURE;

    double result = Math.pow(leftEval.floatValue(), rightEval.floatValue());
    return leftEval.derive((float) result);
  }

  private static CalcEvaluation evaluateHypot(
    CalcFuncMany calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    if (calcValue.args().isEmpty()) return CalcEvaluation.FAILURE;

    CalcEvaluation firstEval = evaluateNode(calcValue.args().get(0), fallbackCalc);
    if (firstEval.isFailure()) return firstEval;

    if (!(
      firstEval.isNumber()
      || firstEval.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE)
    )) return CalcEvaluation.FAILURE;

    double sumOfSquares = Math.pow(firstEval.floatValue(), 2);
    Iterator<CSSValue> argIt = calcValue.args().listIterator(1);
    while (argIt.hasNext()) {
      CalcEvaluation nextEval = evaluateNode(argIt.next(), fallbackCalc);
      if (nextEval.isFailure()) return nextEval;
      if (!firstEval.sameType(nextEval)) return CalcEvaluation.FAILURE;

      sumOfSquares += Math.pow(nextEval.floatValue(), 2);
    }

    return firstEval.derive((float) Math.sqrt(sumOfSquares));
  }

  private static CalcEvaluation evaluateLog(
    CalcLogFunc calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
    CalcEvaluation leftEval = evaluateNode(calcValue.leftArg(), fallbackCalc);
    if (leftEval.isFailure()) return leftEval;
    if (!leftEval.isNumber()) return CalcEvaluation.FAILURE;

    double base = Math.E;
    if (calcValue.rightArg() != null) {
      CalcEvaluation baseEval = evaluateNode(calcValue.rightArg(), fallbackCalc);
      if (baseEval.isFailure()) return baseEval;
      if (!baseEval.isNumber()) return CalcEvaluation.FAILURE;

      base = baseEval.floatValue();
    }

    return leftEval.derive((float) (Math.log(leftEval.floatValue()) / Math.log(base)));
  }

  private static CalcEvaluation evaluateSingleArgMathFunc(
    CalcFuncSingle calcValue, Function<CSSValue,CalcEvaluation> fallbackCalc
  ) {
      CalcEvaluation argEval = evaluateNode(calcValue.arg(), fallbackCalc);
      if (argEval.isFailure()) return CalcEvaluation.FAILURE;

      if (
        !argEval.isNumber()
        && (
          calcValue.type().equals(CalcType.SQRT)
          || calcValue.type().equals(CalcType.EXP))
      ) {
        return CalcEvaluation.FAILURE;
      }

      boolean wasInteger = argEval instanceof CalcNumberEvaluation numEval && numEval.isInteger();
  
      return switch (calcValue.type()) {
        case SQRT -> CalcNumberEvaluation.create(Math.sqrt(argEval.floatValue()), false);
        case EXP -> CalcNumberEvaluation.create(Math.exp(argEval.floatValue()), false);
        case ABS -> argEval.derive(Math.abs(argEval.floatValue()));
        case SIGN -> CalcNumberEvaluation.create(Math.signum(argEval.floatValue()), wasInteger);
        default -> throw new UnsupportedOperationException(
          "Unrecognized single arg math function: " + calcValue.type());
      };
  }

}
