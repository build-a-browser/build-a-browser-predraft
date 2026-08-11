package net.buildabrowser.babbrowser.cssbase.property.table;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum BorderCollapseValue implements CSSValue {
  
  SEPARATE, COLLAPSE;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
