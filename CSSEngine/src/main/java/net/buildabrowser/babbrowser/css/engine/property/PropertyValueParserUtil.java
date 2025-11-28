package net.buildabrowser.babbrowser.css.engine.property;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public final class PropertyValueParserUtil {

  private final CSSFailure NO_VALID_RESULT = new CSSFailure("No valid result...");
  
  private PropertyValueParserUtil() {}

  CSSValue parseLongest(SeekableCSSTokenStream stream, PropertyValueParser... parsers) throws IOException {
    CSSValue longestValue = NO_VALID_RESULT;
    int longestPos = 0;

    int firstPos = stream.position();
    for (PropertyValueParser parser: parsers) {
      CSSValue result = parser.parse(stream, null);
      if (!result.isFailure() && (stream.position() > longestPos || longestValue.isFailure())) {
        longestPos = stream.position();
        longestValue = result;
      }

      stream.seek(firstPos);
    }

    return longestValue;
  };

}
