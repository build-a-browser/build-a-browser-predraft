package net.buildabrowser.babbrowser.cssbase.property.calc;

public interface CalcEvaluation {
  
  // TODO: The spec defines some stuff about types, should probably look more into that
  // Doing ad-hoc for now
  CalcEvalType valueType();

  float floatValue();

  CalcEvaluation derive(float value);

  default boolean isFailure() {
    return valueType().equals(CalcEvalType.FAILURE);
  }

  default boolean isNumber() {
    return valueType().equals(CalcEvalType.NUMBER);
  }

  default boolean sameType(CalcEvaluation other) {
    return other.valueType().equals(valueType());
  }

  enum CalcEvalType {
    NUMBER, LENGTH_PERCENTAGE, ANGLE, FAILURE
  }

  public static record CalcNumberEvaluation(Number value, boolean isInteger) implements CalcEvaluation {

    @Override
    public CalcEvalType valueType() {
      return CalcEvalType.NUMBER;
    }

    @Override
    public float floatValue() {
      return value.floatValue();
    }

    @Override
    public CalcEvaluation derive(float value) {
      boolean canBeInteger = isInteger && value % 1 == 0;
      return new CalcNumberEvaluation(value, canBeInteger);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof CalcNumberEvaluation other) {
        return value.floatValue() == other.value.floatValue() && isInteger == other.isInteger;
      }
      return false;
    }

    public static CalcNumberEvaluation create(Number value, boolean isInteger) {
      return new CalcNumberEvaluation(value, isInteger);
    }

  }

  public static record CalcAngleEvaluation(float value) implements CalcEvaluation {

    @Override
    public CalcEvalType valueType() {
      return CalcEvalType.ANGLE;
    }

    @Override
    public float floatValue() {
      return value;
    }

    @Override
    public CalcEvaluation derive(float value) {
      return new CalcAngleEvaluation(value);
    }

    public static CalcAngleEvaluation create(float value) {
      return new CalcAngleEvaluation(value);
    }

  }

  public static final CalcEvaluation FAILURE = new CalcEvaluation() {

    @Override
    public CalcEvalType valueType() {
      return CalcEvalType.FAILURE;
    }

    @Override
    public float floatValue() {
      throw new UnsupportedOperationException("Can not get value of a failure!");
    }

    @Override
    public CalcEvaluation derive(float value) {
      return FAILURE;
    }
    
  };

}
