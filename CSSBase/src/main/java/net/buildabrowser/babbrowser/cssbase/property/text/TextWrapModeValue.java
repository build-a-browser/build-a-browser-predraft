package net.buildabrowser.babbrowser.cssbase.property.text;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum TextWrapModeValue implements CSSValue {
  
  WRAP, NOWRAP;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
