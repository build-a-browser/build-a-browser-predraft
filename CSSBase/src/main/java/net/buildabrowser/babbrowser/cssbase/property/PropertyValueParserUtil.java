package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class PropertyValueParserUtil {

  private static final CSSFailure NO_VALID_RESULT = new CSSFailure("No valid result...");
  private static final CSSFailure EXPECTED_IDENT = new CSSFailure("Expected an ident token");
  
  private PropertyValueParserUtil() {}

  public static CSSValue parseLongest(SeekableCSSTokenStream stream, PropertyValueParser... parsers) throws IOException {
    CSSValue longestValue = NO_VALID_RESULT;
    int longestPos = stream.position();

    int firstPos = stream.position();
    for (PropertyValueParser parser: parsers) {
      CSSValue result = parser.parse(stream);
      if (!result.isFailure() && (stream.position() > longestPos || longestValue.isFailure())) {
        longestPos = stream.position();
        longestValue = result;
      }

      stream.seek(firstPos);
    }

    stream.seek(longestPos);

    return longestValue;
  }

  public static CSSValue parseOneOrMoreComma(SeekableCSSTokenStream stream, PropertyValueParser parser) throws IOException {
    List<CSSValue> times = new LinkedList<>();
    CSSValue result = parser.parse(stream);
    if (result.isFailure()) return result;
    times.add(result);

    while (stream.peek() instanceof CommaToken) {
      stream.read();
      result = parser.parse(stream);
      if (result.isFailure()) return result;
      times.add(result);
    }

    return new ManyResult(times);
  }

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

      CSSValue result = parser.parse(stream);
      if (result.isFailure()) continue;

      output.values()[i] = result;
      parseAnyOrder(stream, parsers, output);

      return output;
    }

    stream.seek(firstPos);
    return NO_VALID_RESULT;
  }

  public static CSSValue parseCommaRepeat(SeekableCSSTokenStream stream, PropertyValueParser parser) throws IOException {
    // Assumes whitespace already removed
    CSSValue firstValue = parser.parse(stream);
    if (firstValue.isFailure()) return firstValue;

    List<CSSValue> relatedValues = new ArrayList<>();
    relatedValues.add(firstValue);

    while (stream.peek() instanceof CommaToken) {
      stream.read();

      CSSValue nextValue = parser.parse(stream);
      if (nextValue.isFailure()) return nextValue;
      relatedValues.add(nextValue);
    }

    return new ManyResult(relatedValues);
  }

  public static record AnyOrderResult(CSSValue[] values) implements CSSValue {
    
  }

  public static record ManyResult(List<CSSValue> values) implements CSSValue {
    
    public static ManyResult create(CSSValue... values) {
      return new ManyResult(List.of(values));
    }

  }

}
