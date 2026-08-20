package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record BackgroundRepeatValue(
  BackgroundAxisRepeatValue xAxisRepeat, BackgroundAxisRepeatValue yAxisRepeat
) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeMaybeEqual(xAxisRepeat, yAxisRepeat);
  }
  
  public static enum BackgroundAxisRepeatValue implements CSSValue {

    REPEAT, SPACE, ROUND, NO_REPEAT;

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }

  }

  public static BackgroundRepeatValue create(
    BackgroundAxisRepeatValue xAxisRepeat, BackgroundAxisRepeatValue yAxisRepeat
  ) {
    return new BackgroundRepeatValue(xAxisRepeat, yAxisRepeat);
  }

}
