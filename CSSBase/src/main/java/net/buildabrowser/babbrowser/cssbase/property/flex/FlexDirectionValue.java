package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum FlexDirectionValue implements CSSValue {

  ROW, ROW_REVERSE, COLUMN, COLUMN_REVERSE;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
