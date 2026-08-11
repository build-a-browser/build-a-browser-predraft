package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum AlignItemsValue implements CSSValue {

  FLEX_START, FLEX_END, CENTER, BASELINE, STRETCH;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
