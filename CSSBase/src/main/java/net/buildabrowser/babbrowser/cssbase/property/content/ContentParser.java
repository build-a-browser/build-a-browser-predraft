package net.buildabrowser.babbrowser.cssbase.property.content;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.content.ContentValue.StringContentValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ContentParser implements PropertyValueParser {

  // TODO: CSS defines this whole complicated syntax with images and counters and lists and stuff
  // I don't feel like doing that right now
  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    Token token = stream.read();
    if (
      token instanceof IdentToken identToken
      && identToken.value().equals("auto")
    ) {
      return CSSValue.AUTO;
    } else if (
      token instanceof IdentToken identToken
      && identToken.value().equals("normal")
    ) {
      return ContentValue.NORMAL;
    } else if (
      token instanceof StringToken stringToken
    ) {
      return StringContentValue.create(stringToken.value());
    } else {
      return CSSFailure.EXPECTED_STRING;
    }
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.CONTENT;
  }
  
}
