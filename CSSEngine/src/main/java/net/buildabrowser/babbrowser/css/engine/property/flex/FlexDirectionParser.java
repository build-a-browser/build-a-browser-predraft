package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class FlexDirectionParser implements PropertyValueParser {

  private static final Map<String, CSSValue> FLEX_DIRECTION_VALUES = Map.of(
    "row", FlexDirectionValue.ROW,
    "row-reverse", FlexDirectionValue.ROW_REVERSE,
    "column", FlexDirectionValue.COLUMN,
    "column-reverse", FlexDirectionValue.COLUMN_REVERSE
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, FLEX_DIRECTION_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLEX_DIRECTION;
  }
  
}
