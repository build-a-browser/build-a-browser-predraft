package net.buildabrowser.babbrowser.cssbase.property.align;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

// TODO: Support more values
public enum AlignItemsValue implements CSSValue {

  SELF_START, SELF_END, FLEX_START, FLEX_END, CENTER, BASELINE, STRETCH;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }
  
}
