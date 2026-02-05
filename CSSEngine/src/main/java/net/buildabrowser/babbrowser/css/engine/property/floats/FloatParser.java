package net.buildabrowser.babbrowser.css.engine.property.floats;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;


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
