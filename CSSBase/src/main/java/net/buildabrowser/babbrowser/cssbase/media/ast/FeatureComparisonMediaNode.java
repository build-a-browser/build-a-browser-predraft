package net.buildabrowser.babbrowser.cssbase.media.ast;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FeatureComparisonMediaNode(
  MediaFeature feature,
  MediaFeatureComparison comparison,
  CSSValue target
) implements MediaNode {
  
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
