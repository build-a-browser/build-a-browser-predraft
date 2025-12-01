package net.buildabrowser.babbrowser.css.engine.property.floats;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue.FloatSide;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;


public class FloatParser implements PropertyValueParser {

  private static final Map<String, CSSValue> FLOAT_VALUES = Map.of(
    "left", new FloatValue(FloatSide.LEFT),
    "right", new FloatValue(FloatSide.RIGHT),
    "none", CSSValue.NONE,
    "inherit", CSSValue.INHERIT
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseIdentMap(stream, FLOAT_VALUES);
    if (result.isFailure()) return result;

    activeStyles.setFloat(result);
    return result;
  }
  
}
