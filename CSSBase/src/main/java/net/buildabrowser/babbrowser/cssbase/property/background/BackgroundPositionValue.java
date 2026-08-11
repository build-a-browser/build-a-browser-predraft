package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record BackgroundPositionValue(
  BackgroundPositionSide horizontalSide,
  CSSValue horizontalLength,
  BackgroundPositionSide verticalSide,
  CSSValue verticalLength
) implements CSSValue {
  
  public static BackgroundPositionValue create(
    BackgroundPositionSide horizontalSide,
    CSSValue horizontalLength,
    BackgroundPositionSide verticalSide,
    CSSValue verticalLength
  ) {
    return new BackgroundPositionValue(
      horizontalSide, horizontalLength,
      verticalSide, verticalLength);
  }

  public static enum BackgroundPositionSide implements CSSValue {
    
    LEFT, CENTER, RIGHT, TOP, BOTTOM;

    public boolean isHorizontal() {
      return !(equals(TOP) || equals(BOTTOM));
    }

    public boolean isVertical() {
      return !(equals(LEFT) || equals(RIGHT));
    }

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }

  }

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(
      horizontalSide, horizontalLength,
      verticalSide, verticalLength);
  }

}
