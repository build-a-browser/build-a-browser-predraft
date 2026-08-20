package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum BackgroundSizeValue implements CSSValue {
  
  COVER, CONTAIN;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

  public static record SizedBackgroundSizeValue(
    CSSValue widthValue, CSSValue heightValue
  ) implements CSSValue {

    @Override
    public String serialize() {
      if (heightValue.equals(CSSValue.AUTO)) {
        return CSSSerializerUtil.serializeValue(widthValue);
      } else {
        return CSSSerializerUtil.serializeManySpaces(widthValue, heightValue);
      }
    }

    public static SizedBackgroundSizeValue create(
      CSSValue widthValue, CSSValue heightValue
    ) {
      return new SizedBackgroundSizeValue(widthValue, heightValue);
    }
    
  }

}
