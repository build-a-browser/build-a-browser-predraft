package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public interface CalcValue extends CSSValue {
  
  CalcType type();

  enum CalcType {
    CALC, MIN, MAX, CLAMP, ROUND, MOD, REM, SIN, COS, TAN, ASIN,
    ACOS, ATAN, ATAN2, POW, SQRT, HYPOT, LOG, EXP, ABS, SIGN,
    ADD, SUB, MUL, DIV
  }

  enum CalcKeyword implements CSSValue {
    E((float) Math.E),
    PI((float) Math.PI),
    INFINITY(Float.POSITIVE_INFINITY),
    NEG_INFINITY(Float.NEGATIVE_INFINITY),
    NaN(Float.NaN);

    private float value;

    private CalcKeyword(float value) {
      this.value = value;
    }

    public float value() {
      return this.value;
    }
  }

  enum RoundingStrategy {
    NEAREST, UP, DOWN, TO_ZERO, LINE_WIDTH;
  }

  // TODO: Add create method - we could maybe cache repetitive nodes!
  record CalcFuncSingle(CalcType type, CSSValue arg) implements CalcValue {}
  record CalcFuncDouble(CalcType type, CSSValue leftArg, CSSValue rightArg) implements CalcValue {}
  record CalcFuncMany(CalcType type, List<CSSValue> args) implements CalcValue {}
  record CalcClampFunc(CalcType type, CSSValue minValue, CSSValue idealValue, CSSValue maxValue) implements CalcValue {}
  record CalcRoundFunc(CalcType type, RoundingStrategy roundingStrategy, CSSValue a, CSSValue b) implements CalcValue {}
  record CalcLogFunc(CalcType type, CSSValue leftArg, CSSValue rightArg) implements CalcValue {}
  record CalcNumber(Number value, boolean isInteger) implements CSSValue {}

}
