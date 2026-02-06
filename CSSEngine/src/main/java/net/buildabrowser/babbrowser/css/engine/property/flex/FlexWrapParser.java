package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class FlexWrapParser implements PropertyValueParser {

  private static final Map<String, CSSValue> FLEX_WRAP_VALUES = Map.of(
    "nowrap", FlexWrapValue.NOWRAP,
    "wrap", FlexWrapValue.WRAP,
    "wrap-reverse", FlexWrapValue.WRAP_REVERSE
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, FLEX_WRAP_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLEX_WRAP;
  }
  
}
