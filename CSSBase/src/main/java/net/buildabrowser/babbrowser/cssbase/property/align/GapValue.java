package net.buildabrowser.babbrowser.cssbase.property.align;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum GapValue implements CSSValue {
  
  NORMAL;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
