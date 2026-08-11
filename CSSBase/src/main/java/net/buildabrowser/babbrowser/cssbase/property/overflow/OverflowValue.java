package net.buildabrowser.babbrowser.cssbase.property.overflow;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum OverflowValue implements CSSValue {
  
  VISIBLE, HIDDEN, CLIP, SCROLL;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
