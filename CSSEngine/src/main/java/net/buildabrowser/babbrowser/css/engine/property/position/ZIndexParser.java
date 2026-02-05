package net.buildabrowser.babbrowser.css.engine.property.position;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ZIndexParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_INTEGER = new CSSFailure("Expected an integer token");

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    Token token = stream.read();
    CSSValue value = null;
    if (
      token instanceof IdentToken identToken
      && identToken.value().equals("auto")
    ) {
      value = CSSValue.AUTO;
    } else if (
      token instanceof NumberToken numberToken
      && numberToken.isInteger()
    ) {
      value = ZIndexValue.create(numberToken.value().intValue());
    }

    return value == null ? EXPECTED_INTEGER : value;
  }
  
  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.Z_INDEX;
  }

}
