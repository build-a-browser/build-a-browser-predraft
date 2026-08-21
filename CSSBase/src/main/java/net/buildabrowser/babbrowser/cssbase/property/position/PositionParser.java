package net.buildabrowser.babbrowser.cssbase.property.position;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class PositionParser implements PropertyValueParser {

  private static final Map<String, CSSValue> POSITION_VALUES = Map.of(
    "static", PositionValue.STATIC,
    "relative", PositionValue.RELATIVE,
    "absolute", PositionValue.ABSOLUTE,
    "sticky", PositionValue.STICKY,
    "fixed", PositionValue.FIXED
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, POSITION_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.POSITION;
  }
  
}
