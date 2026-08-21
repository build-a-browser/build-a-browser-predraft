package net.buildabrowser.babbrowser.cssbase.property.text;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;


public class TextWrapModeParser implements PropertyValueParser {

  private static final Map<String, CSSValue> TEXT_WRAP_MODE_VALUES = Map.of(
    "wrap", TextWrapModeValue.WRAP,
    "nowrap", TextWrapModeValue.NOWRAP
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, TEXT_WRAP_MODE_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.TEXT_WRAP_MODE;
  }
  
}
