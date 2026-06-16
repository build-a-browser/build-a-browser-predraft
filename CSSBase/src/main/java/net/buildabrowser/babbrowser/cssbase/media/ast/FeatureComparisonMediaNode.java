package net.buildabrowser.babbrowser.cssbase.media.ast;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;

public record FeatureComparisonMediaNode(
  MediaFeature feature,
  MediaFeatureComparison comparison,
  CSSValue target
) implements MediaNode {
  
  @Override
  public boolean resolve(MediaContext context) {
    return switch (feature) {
      case WIDTH -> compareLength(context.docWidth(), context);
      case HEIGHT -> compareLength(context.docHeight(), context);
      default -> throw new UnsupportedOperationException(
        "Unrecognized feature: " + feature);
    };
  }

  private boolean compareLength(int realValue, MediaContext context) {
    CalcEvaluation lengthValue = CalcInterpreter.evaluateNode(target, context.calcFallback());
    if (lengthValue.isFailure()) return false;
    if (
      !lengthValue.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE)
    ) return false;

    return switch (comparison) {
      // TODO: Account for floating point precision
      case EQ -> realValue == lengthValue.floatValue();
      case GT -> realValue > lengthValue.floatValue();
      case GTE -> realValue >= lengthValue.floatValue();
      case LT -> realValue < lengthValue.floatValue();
      case LTE -> realValue <= lengthValue.floatValue();
      case NEQ -> realValue != lengthValue.floatValue();
      default -> throw new UnsupportedOperationException(
        "Unrecognized comparison: " + comparison);
    };
  }

  public static enum MediaFeatureComparison {
    LT, LTE, EQ, GT, GTE, NEQ
  }

  public static FeatureComparisonMediaNode create(
    MediaFeature feature,
    MediaFeatureComparison comparison,
    CSSValue target
  ) {
    return new FeatureComparisonMediaNode(feature, comparison, target);
  }

}
