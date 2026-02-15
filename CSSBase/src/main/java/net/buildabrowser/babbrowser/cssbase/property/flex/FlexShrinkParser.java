package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class FlexShrinkParser implements PropertyValueParser {

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (!(
      stream.read() instanceof NumberToken numberToken
      && numberToken.value().floatValue() >= 0
    )) {
      return CSSFailure.EXPECTED_POSITIVE_NUMBER;
    }

    return new FlexShrinkValue(numberToken.value());
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLEX_SHRINK;
  }
  
}
