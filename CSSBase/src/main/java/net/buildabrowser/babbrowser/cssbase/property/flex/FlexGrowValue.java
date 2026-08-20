package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FlexGrowValue(Number value) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serialize(value);
  }
 
  public static FlexGrowValue create(Number value) {
    return new FlexGrowValue(value);
  }

}
