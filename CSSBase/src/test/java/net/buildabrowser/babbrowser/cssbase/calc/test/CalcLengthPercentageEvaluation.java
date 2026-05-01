package net.buildabrowser.babbrowser.cssbase.calc.test;

import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;

public record CalcLengthPercentageEvaluation(float value) implements CalcEvaluation {
  
  @Override
  public CalcEvalType valueType() {
    return CalcEvalType.LENGTH_PERCENTAGE;
  }

  @Override
  public float floatValue() {
    return value;
  }

  @Override
  public CalcEvaluation derive(float value) {
    return new CalcLengthPercentageEvaluation(value);
  }
  
}
