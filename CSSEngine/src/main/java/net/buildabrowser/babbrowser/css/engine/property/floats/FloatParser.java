package net.buildabrowser.babbrowser.css.engine.property.floats;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;


public class FloatParser implements PropertyValueParser {

  private static final Map<String, CSSValue> FLOAT_VALUES = Map.of(
    "left", FloatValue.LEFT,
    "right", FloatValue.RIGHT,
    "none", CSSValue.NONE,
    "inherit", CSSValue.INHERIT
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseIdentMap(stream, FLOAT_VALUES);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;

    activeStyles.setProperty(CSSProperty.FLOAT, result);
    return result;
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLOAT;
  }
  
}
