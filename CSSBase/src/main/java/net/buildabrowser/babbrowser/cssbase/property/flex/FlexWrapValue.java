package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum FlexWrapValue implements CSSValue {
  
  NOWRAP, WRAP, WRAP_REVERSE;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
