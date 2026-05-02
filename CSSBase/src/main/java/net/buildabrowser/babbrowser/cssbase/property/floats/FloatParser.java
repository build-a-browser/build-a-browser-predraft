package net.buildabrowser.babbrowser.cssbase.property.floats;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;


public class FloatParser implements PropertyValueParser {

  private static final Map<String, CSSValue> FLOAT_VALUES = Map.of(
    "left", FloatValue.LEFT,
    "right", FloatValue.RIGHT,
    "none", CSSValue.NONE,
    "inherit", CSSValue.INHERIT
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, FLOAT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLOAT;
  }
  
}
