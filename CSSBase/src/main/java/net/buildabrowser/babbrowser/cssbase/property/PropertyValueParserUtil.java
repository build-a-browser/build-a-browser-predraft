package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
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

  public static CSSValue parseCommaRepeat(
    SeekableCSSTokenStream stream, PropertyValueParser parser
  ) throws IOException {
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

    return ManyResult.createCommas(relatedValues);
  }

  public static CSSValue parseOneOrMore(
    SeekableCSSTokenStream stream, PropertyValueParser parser
  ) throws IOException {
    // Assumes whitespace already removed
    CSSValue firstValue = parser.parse(stream);
    if (firstValue.isFailure()) return firstValue;

    List<CSSValue> relatedValues = new ArrayList<>();
    relatedValues.add(firstValue);

    while (true) {
      int posMark = stream.position();
      CSSValue nextValue = parser.parse(stream);
      if (nextValue.isFailure()) {
        stream.seek(posMark);
        return ManyResult.createSpaces(relatedValues);
      }
      relatedValues.add(nextValue);
    }
  }

  public static CSSValue parseMaybe(
    SeekableCSSTokenStream stream, PropertyValueParser parser
  ) throws IOException {
    int position = stream.position();
    CSSValue value = parser.parse(stream);
    if (value.isFailure()) {
      stream.seek(position);
    }

    return value;
  }

  public static record AnyOrderResult(CSSValue[] values) implements CSSValue {

    @Override
    public String serialize() {
      StringBuilder serialBuilder = new StringBuilder();
      for (CSSValue value: values) {
        if (value == null) continue;
        if (!serialBuilder.isEmpty()) {
          serialBuilder.append(' ');
        }
        serialBuilder.append(value.serialize());
      }
      return serialBuilder.toString();
    }

  }

  public static record ManyResult(List<CSSValue> values, boolean includeCommas) implements CSSValue {
    
    public static ManyResult createCommas(CSSValue... values) {
      return new ManyResult(List.of(values), true);
    }

    public static ManyResult createCommas(List<CSSValue> values) {
      return new ManyResult(values, true);
    }

    public static ManyResult createSpaces(CSSValue... values) {
      return new ManyResult(List.of(values), false);
    }

    public static ManyResult createSpaces(List<CSSValue> values) {
      return new ManyResult(values, false);
    }

    @Override
    public String serialize() {
      StringJoiner joiner = new StringJoiner(includeCommas ? ", " : " ");
      for (CSSValue value: values) {
        joiner.add(value.serialize());
      }

      return joiner.toString();
    }

  }

}
