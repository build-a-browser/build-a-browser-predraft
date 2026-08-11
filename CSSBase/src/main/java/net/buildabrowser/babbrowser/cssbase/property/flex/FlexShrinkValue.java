package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FlexShrinkValue(Number value) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serialize(value);
  }
 
  public static FlexShrinkValue create(Number value) {
    return new FlexShrinkValue(value);
  }

}
