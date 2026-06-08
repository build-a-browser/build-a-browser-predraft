package net.buildabrowser.babbrowser.cssbase.media.ast;

public record FeatureExistsMediaNode(MediaFeature feature) implements MediaNode {

  public static FeatureExistsMediaNode create(MediaFeature feature) {
    return new FeatureExistsMediaNode(feature);
  }
  
}
