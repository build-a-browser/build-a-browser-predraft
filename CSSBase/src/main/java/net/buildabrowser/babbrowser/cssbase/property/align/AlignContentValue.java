package net.buildabrowser.babbrowser.cssbase.property.align;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

// TODO: Support more values
public enum AlignContentValue implements CSSValue {

  NORMAL, START, END, FLEX_START, FLEX_END, CENTER,
  SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY, STRETCH;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
