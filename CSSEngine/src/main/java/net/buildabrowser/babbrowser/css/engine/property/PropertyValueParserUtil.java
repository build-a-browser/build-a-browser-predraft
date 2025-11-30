package net.buildabrowser.babbrowser.css.engine.property;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class PropertyValueParserUtil {

  private static final CSSFailure NO_VALID_RESULT = new CSSFailure("No valid result...");
  private static final CSSFailure EXPECTED_IDENT = new CSSFailure("Expected an ident token");
  
  private PropertyValueParserUtil() {}

  public static CSSValue parseLongest(SeekableCSSTokenStream stream, PropertyValueParser... parsers) throws IOException {
    CSSValue longestValue = NO_VALID_RESULT;
    int longestPos = stream.position();

    int firstPos = stream.position();
    System.out.println(firstPos);
    for (PropertyValueParser parser: parsers) {
      CSSValue result = parser.parse(stream, null);
      if (!result.isFailure() && (stream.position() > longestPos || longestValue.isFailure())) {
        longestPos = stream.position();
        longestValue = result;
      }

      stream.seek(firstPos);
    }

    stream.seek(longestPos);

    return longestValue;
  };

  public static CSSValue parseIdentMap(SeekableCSSTokenStream stream, Map<String, CSSValue> options) throws IOException {
    if (!(stream.read() instanceof IdentToken identToken)) {
      return EXPECTED_IDENT;
    }

    return options.getOrDefault(identToken.value(), NO_VALID_RESULT);
  }

  public static CSSValue parseAnyOrder(SeekableCSSTokenStream stream, PropertyValueParser... parsers) throws IOException {
    return parseAnyOrder(stream, parsers, new AnyOrderResult(new CSSValue[parsers.length]));
  }

  private static CSSValue parseAnyOrder(SeekableCSSTokenStream stream, PropertyValueParser[] parsers, AnyOrderResult output) throws IOException {
    int firstPos = stream.position();

    for (int i = 0; i < parsers.length; i++) {
      PropertyValueParser parser = parsers[i];
      if (output.values()[i] != null) continue;

      stream.seek(firstPos);

      CSSValue result = parser.parse(stream, null);
      if (result.isFailure()) continue;

      output.values()[i] = result;
      parseAnyOrder(stream, parsers, output);

      return output;
    }

    stream.seek(firstPos);
    return NO_VALID_RESULT;
  }

  public static record AnyOrderResult(CSSValue[] values) implements CSSValue {
    
  }

}
