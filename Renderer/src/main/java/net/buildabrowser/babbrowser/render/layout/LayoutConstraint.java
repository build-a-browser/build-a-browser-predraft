package net.buildabrowser.babbrowser.render.layout;

import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;

public record LayoutConstraint(LayoutConstraintType type, float value) implements CalcEvaluation {
  
  public static final LayoutConstraint MIN_CONTENT = new LayoutConstraint(LayoutConstraintType.MIN_CONTENT, -1);
  public static final LayoutConstraint MAX_CONTENT = new LayoutConstraint(LayoutConstraintType.MAX_CONTENT, -1);
  public static final LayoutConstraint AUTO = new LayoutConstraint(LayoutConstraintType.AUTO, -1);

  public static LayoutConstraint of(float value) {
    return new LayoutConstraint(LayoutConstraintType.BOUNDED, value);
  }

  public boolean isPreLayoutConstraint() {
    return
      !type.equals(LayoutConstraintType.BOUNDED)
      && !type.equals(LayoutConstraintType.AUTO);
  }

  public boolean isBounded() {
    return type.equals(LayoutConstraintType.BOUNDED);
  }

  @Override
  public CalcEvalType valueType() {
    return isBounded() ?
      CalcEvalType.LENGTH_PERCENTAGE :
      CalcEvalType.FAILURE;
  }

  @Override
  public float floatValue() {
    if (!isBounded()) {
      throw new UnsupportedOperationException("Cannot do calculations on unbounded layout constraint!");
    }
    return value;
  }

  @Override
  public CalcEvaluation derive(float value) {
    return LayoutConstraint.of(value);
  }

  public static enum LayoutConstraintType {
    BOUNDED, AUTO, MIN_CONTENT, MAX_CONTENT
  }

}
