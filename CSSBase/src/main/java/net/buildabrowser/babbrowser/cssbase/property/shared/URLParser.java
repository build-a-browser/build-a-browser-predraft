package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;
import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.URLToken;

public class URLParser implements PropertyValueParser {

  private static final CSSValue EXPECTED_URL_FUNCTION = new CSSFailure("Expected a url() or src() function value!");

  // TODO: Support url-modifier

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    URI refURL = stream.source().sourceURL();
    if (
      stream.peek() instanceof FunctionValue functionValue
      && (functionValue.name().equals("url") || functionValue.name().equals("src"))
    ) {
      stream.read();
      CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
        stream.source(), functionValue.value());
      Token urlName = innerStream.read();
      if (!(urlName instanceof StringToken stringToken)) {
        return CSSFailure.EXPECTED_STRING;
      }
      if (!(innerStream.peek() instanceof EOFToken)) {
        return CSSFailure.EXPECTED_EOF;
      }

      // TODO: Needs to be relative to the source stylesheet
      return URLValue.create(stringToken.value(), refURL);
    } else if (stream.peek() instanceof URLToken urlToken) {
      stream.read();
      return URLValue.create(urlToken.value(), refURL);
    } else {
      // TODO: Also parse URL exception
      return EXPECTED_URL_FUNCTION;
    }
  }
  
}