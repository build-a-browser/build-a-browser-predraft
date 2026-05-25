package net.buildabrowser.babbrowser.cssbase.property.table;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class TableLayoutParser implements PropertyValueParser {

  private static final Map<String, CSSValue> TABLE_LAYOUT_VALUES = Map.of(
    "auto", CSSValue.AUTO,
    "fixed", TableLayoutValue.FIXED
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, TABLE_LAYOUT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.TABLE_LAYOUT;
  }
  
}
