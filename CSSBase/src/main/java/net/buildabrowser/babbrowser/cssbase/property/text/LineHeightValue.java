package net.buildabrowser.babbrowser.cssbase.property.text;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum LineHeightValue implements CSSValue {
  
  NORMAL;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

  public static record NumberHeight(Number multiplier) implements CSSValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.serialize(multiplier);
    }

    public static NumberHeight create(Number multiplier) {
      return new NumberHeight(multiplier);
    }

  }

}
