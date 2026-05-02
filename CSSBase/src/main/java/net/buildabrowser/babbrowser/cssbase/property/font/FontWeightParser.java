package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightValue.RelativeFontWeightValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class FontWeightParser implements PropertyValueParser {

  private Map<String, CSSValue> NAMED_WEIGHTS = Map.of(
    "normal", FontWeightValue.create(400),
    "bold", FontWeightValue.create(700),
    "bolder", RelativeFontWeightValue.BOLDER,
    "lighter", RelativeFontWeightValue.LIGHTER
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    Token token = stream.read();
    if (
      token instanceof IdentToken identToken
      && NAMED_WEIGHTS.containsKey(identToken.value())
    ) {
      return NAMED_WEIGHTS.get(identToken.value());
    } else if (
      token instanceof NumberToken numberToken
      && numberToken.isInteger()
      && numberToken.value().intValue() >= 1
      && numberToken.value().intValue() <= 1000
    ) {
      return FontWeightValue.create(numberToken.value().intValue());
    } else {
      return CSSFailure.EXPECTED_INTEGER;
    }
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FONT_WEIGHT;
  }
  
}
