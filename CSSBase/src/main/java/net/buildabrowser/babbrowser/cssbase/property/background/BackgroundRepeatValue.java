package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record BackgroundRepeatValue(
  BackgroundAxisRepeatValue xAxisRepeat, BackgroundAxisRepeatValue yAxisRepeat
) implements CSSValue {
  
  public static enum BackgroundAxisRepeatValue implements CSSValue {
    REPEAT, SPACE, ROUND, NO_REPEAT
  }

  public static BackgroundRepeatValue create(
    BackgroundAxisRepeatValue xAxisRepeat, BackgroundAxisRepeatValue yAxisRepeat
  ) {
    return new BackgroundRepeatValue(xAxisRepeat, yAxisRepeat);
  }

}
