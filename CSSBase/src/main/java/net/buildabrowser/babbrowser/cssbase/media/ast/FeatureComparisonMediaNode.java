package net.buildabrowser.babbrowser.cssbase.media.ast;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValueOrFeature;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcEvalType;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;

public record FeatureComparisonMediaNode(
  CSSValueOrFeature feature,
  MediaFeatureComparison comparison,
  CSSValueOrFeature target
) implements MediaNode {
  
  @Override
  public boolean resolve(MediaContext context) {
    float valueA = resolveFeatureOrValue(feature, context);
    float valueB = resolveFeatureOrValue(target, context);
    return compareFloats(comparison, valueA, valueB);
  }

  public static FeatureComparisonMediaNode create(
    CSSValueOrFeature feature,
    MediaFeatureComparison comparison,
    CSSValueOrFeature target
  ) {
    return new FeatureComparisonMediaNode(feature, comparison, target);
  }
 
  public static boolean compareFloats(
    MediaFeatureComparison comparison,
    float valueA, float valueB
  ) {
    return switch (comparison) {
      // TODO: Account for floating point precision
      case EQ -> valueA == valueB;
      case GT -> valueA > valueB;
      case GTE -> valueA >= valueB;
      case LT -> valueA < valueB;
      case LTE -> valueA <= valueB;
      case NEQ -> valueA != valueB;
      default -> throw new UnsupportedOperationException(
        "Unrecognized comparison: " + comparison);
    };
  }

  public static float resolveFeatureOrValue(CSSValueOrFeature value, MediaContext context) {
    return switch (value) {
      case MediaFeature.WIDTH -> context.docWidth();
      case MediaFeature.HEIGHT -> context.docHeight();
      default -> resolveValue((CSSValue) value, context);
    };
  }

  private static Float resolveValue(CSSValue value, MediaContext context) {
    CalcEvaluation lengthValue = CalcInterpreter.evaluateNode(value, context.calcFallback());
    if (lengthValue.isFailure()) return null;
    if (
      !lengthValue.valueType().equals(CalcEvalType.LENGTH_PERCENTAGE)
    ) return Float.NaN;
    
    return lengthValue.floatValue();
  }

  public static enum MediaFeatureComparison {
    LT, LTE, EQ, GT, GTE, NEQ
  }

}
