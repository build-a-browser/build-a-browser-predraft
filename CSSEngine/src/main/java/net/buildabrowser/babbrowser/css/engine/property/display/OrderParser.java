package net.buildabrowser.babbrowser.css.engine.property.display;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class OrderParser implements PropertyValueParser {

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (!(
      stream.read() instanceof NumberToken numberToken
      && numberToken.isInteger()
    )) {
      return CSSFailure.EXPECTED_INTEGER;
    }

    return OrderValue.create(numberToken.value().intValue());
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.ORDER;
  }
  
}
