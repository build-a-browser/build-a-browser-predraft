package net.buildabrowser.babbrowser.cssbase.media.ast;

public enum MediaFeature {
  WIDTH(true), HEIGHT(true);

  private final boolean allowMinMax;

  private MediaFeature(boolean allowMinMax) {
    this.allowMinMax = allowMinMax;
  }

  public boolean allowMinMax() {
    return this.allowMinMax;
  }

  public static MediaFeature byName(String name) {
    return switch (name.toLowerCase()) {
      case "width" -> MediaFeature.WIDTH;
      case "height" -> MediaFeature.HEIGHT;
      default -> null;
    };
  }
}
