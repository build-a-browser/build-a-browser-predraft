package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FontWeightValue(int weight) implements CSSValue {
  
  @Override
  public String serialize() {
    return String.valueOf(weight);
  }

  public static FontWeightValue create(int weight) {
    return new FontWeightValue(weight);
  }

  public static enum RelativeFontWeightValue implements CSSValue {

    BOLDER, LIGHTER;

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }
    
  }

}
