package net.buildabrowser.babbrowser.cssbase.property.floats;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum FloatValue implements CSSValue {

  LEFT, RIGHT;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}