package net.buildabrowser.babbrowser.css.engine.property.position;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class PositionParser implements PropertyValueParser {

  private static final Map<String, CSSValue> POSITION_VALUES = Map.of(
    "static", PositionValue.STATIC,
    "relative", PositionValue.RELATIVE,
    "absolute", PositionValue.ABSOLUTE,
    "sticky", PositionValue.STICKY,
    "fixed", PositionValue.FIXED
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, POSITION_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.POSITION;
  }
  
}
