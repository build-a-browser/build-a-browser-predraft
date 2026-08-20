package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum FlexBasisValue implements CSSValue {

  CONTENT;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
