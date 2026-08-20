package net.buildabrowser.babbrowser.cssbase.property.display;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record DisplayValue(OuterDisplayValue outerDisplayValue, InnerDisplayValue innerDisplayValue) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(outerDisplayValue, innerDisplayValue);
  }
  
  public static enum OuterDisplayValue implements CSSValue {
    BLOCK, INLINE, RUN_IN, CONTENTS, NONE,
    TABLE_ROW_GROUP, TABLE_HEADER_GROUP, TABLE_FOOTER_GROUP, TABLE_ROW, TABLE_CELL,
    TABLE_COLUMN_GROUP, TABLE_COLUMN, TABLE_CAPTION;

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }
  }

  public static enum InnerDisplayValue implements CSSValue {
    FLOW, FLOW_ROOT, TABLE, FLEX, GRID, RUBY,
    TABLE_ROW_GROUP, TABLE_HEADER_GROUP, TABLE_FOOTER_GROUP, TABLE_ROW,
    TABLE_COLUMN_GROUP, TABLE_COLUMN;

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }
  }

  public static DisplayValue create(OuterDisplayValue outerDisplayValue, InnerDisplayValue innerDisplayValue) {
    return new DisplayValue(outerDisplayValue, innerDisplayValue);
  }

}
