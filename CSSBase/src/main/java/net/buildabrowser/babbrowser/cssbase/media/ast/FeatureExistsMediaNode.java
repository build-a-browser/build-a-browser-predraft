package net.buildabrowser.babbrowser.cssbase.media.ast;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;

public record FeatureExistsMediaNode(MediaFeature feature) implements MediaNode {

  @Override
  public boolean resolve(MediaContext context) {
    return switch (feature) {
      case HEIGHT -> context.docHeight() > 0;
      case WIDTH -> context.docWidth() > 0;
      default -> throw new UnsupportedOperationException(
        "Unrecognized feature: " + feature);
    };
  }

  public static FeatureExistsMediaNode create(MediaFeature feature) {
    return new FeatureExistsMediaNode(feature);
  }
  
}
