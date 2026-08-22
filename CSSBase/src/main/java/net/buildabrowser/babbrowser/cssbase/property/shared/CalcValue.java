package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public interface CalcValue extends CSSValue {
  
  CalcType type();

  enum CalcType {
    CALC, MIN, MAX, CLAMP, ROUND, MOD, REM, SIN, COS, TAN, ASIN,
    ACOS, ATAN, ATAN2, POW, SQRT, HYPOT, LOG, EXP, ABS, SIGN,
    ADD, SUB, MUL, DIV
  }

  enum CalcKeyword implements CSSValue {
    E((float) Math.E, "e"),
    PI((float) Math.PI, "pi"),
    INFINITY(Float.POSITIVE_INFINITY, "infinity"),
    NEG_INFINITY(Float.NEGATIVE_INFINITY, "infinity"),
    NaN(Float.NaN, "NaN");

    private final float value;
    private final String serialized;

    private CalcKeyword(float value, String serialized) {
      this.value = value;
      this.serialized = serialized;
    }

    public float value() {
      return this.value;
    }

    @Override
    public String serialize() {
      return this.serialized;
    }
  }

  enum RoundingStrategy implements CSSValue {

    NEAREST, UP, DOWN, TO_ZERO, LINE_WIDTH;

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }

  }

  record CalcFuncSingle(CalcType type, CSSValue arg) implements CalcValue {
    @Override
    public String serialize() {
      return CSSSerializerUtil.formatFunction(
        CSSSerializerUtil.serializeEnum(type), arg);
    }
  }

  record CalcFuncDouble(CalcType type, CSSValue leftArg, CSSValue rightArg) implements CalcValue {
    @Override
    public String serialize() {
      String op = switch (type) {
        case ADD -> " + ";
        case SUB -> " - ";
        case MUL -> " * ";
        case DIV -> " / ";
        default -> null;
      };

      if (op != null) {
        return String.join("",
          "(", CSSSerializerUtil.serializeValue(leftArg),
          op, CSSSerializerUtil.serializeValue(rightArg), ")");
      }

      return CSSSerializerUtil.formatFunction(CSSSerializerUtil.serializeEnum(type), leftArg, rightArg);
    }
  }

  record CalcFuncMany(CalcType type, List<CSSValue> args) implements CalcValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.formatFunction(
        CSSSerializerUtil.serializeEnum(type),
        args.toArray(new CSSValue[0]));
    }
    
  }

  record CalcClampFunc(CalcType type, CSSValue minValue, CSSValue idealValue, CSSValue maxValue) implements CalcValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.formatFunction(
        CSSSerializerUtil.serializeEnum(type), 
        minValue, idealValue, maxValue);
    }

  }

  record CalcRoundFunc(CalcType type, RoundingStrategy roundingStrategy, CSSValue a, CSSValue b) implements CalcValue {
    @Override
    public String serialize() {
      return CSSSerializerUtil.formatFunction(
        CSSSerializerUtil.serializeEnum(type), 
        roundingStrategy, a, b);
    }
  }

  record CalcLogFunc(CalcType type, CSSValue leftArg, CSSValue rightArg) implements CalcValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.formatFunction(
        CSSSerializerUtil.serializeEnum(type), 
        leftArg, rightArg);
    }

  }

  record CalcNumber(Number value, boolean isInteger) implements CSSValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.serialize(value);
    }

  }

}
