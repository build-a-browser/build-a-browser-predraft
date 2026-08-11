package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FontNameValue(String name) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeString(name);
  }
 
  public static FontNameValue create(String name) {
    return new FontNameValue(name);
  }

}
