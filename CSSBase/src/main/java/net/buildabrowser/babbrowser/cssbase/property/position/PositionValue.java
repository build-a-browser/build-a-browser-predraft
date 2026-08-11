package net.buildabrowser.babbrowser.cssbase.property.position;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum PositionValue implements CSSValue {
  
  STATIC, RELATIVE, ABSOLUTE, STICKY, FIXED;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
