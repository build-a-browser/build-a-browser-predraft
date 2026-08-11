package net.buildabrowser.babbrowser.cssbase.property.table;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum CaptionSideValue implements CSSValue {
  
  TOP, BOTTOM;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
