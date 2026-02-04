package net.buildabrowser.babbrowser.css.engine.property.text;

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


public class TextWrapModeParser implements PropertyValueParser {

  private static final Map<String, CSSValue> TEXT_WRAP_MODE_VALUES = Map.of(
    "wrap", TextWrapModeValue.WRAP,
    "nowrap", TextWrapModeValue.NO_WRAP
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseIdentMap(stream, TEXT_WRAP_MODE_VALUES);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;

    activeStyles.setProperty(CSSProperty.TEXT_WRAP_MODE, result);
    return result;
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.TEXT_WRAP_MODE;
  }
  
}
