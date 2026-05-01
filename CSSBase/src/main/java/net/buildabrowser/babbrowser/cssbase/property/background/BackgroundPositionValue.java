package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;

public record BackgroundPositionValue(
  BackgroundPositionSide horizontalSide,
  PercentageValue horizontalPercent,
  BackgroundPositionSide verticalSide,
  PercentageValue verticalPercent
) implements CSSValue {
  
  public static BackgroundPositionValue create(
    BackgroundPositionSide horizontalSide,
    PercentageValue horizontalPercent,
    BackgroundPositionSide verticalSide,
    PercentageValue verticalPercent
  ) {
    return new BackgroundPositionValue(
      horizontalSide, horizontalPercent,
      verticalSide, verticalPercent);
  }

  public static enum BackgroundPositionSide implements CSSValue {
    
    LEFT, CENTER, RIGHT, TOP, BOTTOM;

    public boolean isHorizontal() {
      return !(equals(TOP) || equals(BOTTOM));
    }

    public boolean isVertical() {
      return !(equals(LEFT) || equals(RIGHT));
    }

  }

}
