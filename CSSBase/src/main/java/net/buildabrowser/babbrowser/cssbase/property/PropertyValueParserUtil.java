package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class PropertyValueParserUtil {

  private static final CSSFailure NO_VALID_RESULT = new CSSFailure("No valid result...");
  private static final CSSFailure EXPECTED_IDENT = new CSSFailure("Expected an ident token");
  
  private PropertyValueParserUtil() {}

  public static CSSValue parseLongest(CSSTokenStream stream, PropertyValueParser... parsers) throws IOException {
    CSSValue longestValue = NO_VALID_RESULT;
    int longestPos = stream.mark();

    for (PropertyValueParser parser: parsers) {
      int mark = stream.mark();
      CSSValue result = parser.parse(stream);
      if (
        !result.isFailure()
        && (stream.nextMark() > longestPos || longestValue.isFailure())
      ) {
        longestPos = stream.nextMark();
        longestValue = result;
      }

      stream.restoreMark(mark);
    }

    stream.seek(longestPos);
    stream.discardMark();

    return longestValue;
  }

  public static CSSValue parseIdentMap(CSSTokenStream stream, Map<String, CSSValue> options) throws IOException {
    if (!(stream.read() instanceof IdentToken identToken)) {
      return EXPECTED_IDENT;
    }

    return options.getOrDefault(identToken.value(), NO_VALID_RESULT);
  }

  public static CSSValue parseAnyOrder(CSSTokenStream stream, PropertyValueParser... parsers) throws IOException {
    return parseAnyOrder(stream, parsers, new AnyOrderResult(new CSSValue[parsers.length]));
  }

  private static CSSValue parseAnyOrder(CSSTokenStream stream, PropertyValueParser[] parsers, AnyOrderResult output) throws IOException {
    int mark = stream.mark();

    for (int i = 0; i < parsers.length; i++) {
      PropertyValueParser parser = parsers[i];
      if (output.values()[i] != null) continue;

      stream.restoreMark(mark);
      mark = stream.mark();

      CSSValue result = parser.parse(stream);
      if (result.isFailure()) continue;

      stream.discardMark();
      output.values()[i] = result;
      parseAnyOrder(stream, parsers, output);

      return output;
    }

    stream.restoreMark(mark);
    return NO_VALID_RESULT;
  }

  public static CSSValue parseCommaRepeat(
    CSSTokenStream stream, PropertyValueParser parser
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
    CSSTokenStream stream, PropertyValueParser parser
  ) throws IOException {
    // Assumes whitespace already removed
    CSSValue firstValue = parser.parse(stream);
    if (firstValue.isFailure()) return firstValue;

    List<CSSValue> relatedValues = new ArrayList<>();
    relatedValues.add(firstValue);

    return repeatParse(stream, parser, relatedValues);
  }

  public static CSSValue parseZeroOrMore(
    CSSTokenStream stream, PropertyValueParser parser
  ) throws IOException {
    // Assumes whitespace already removed
    List<CSSValue> relatedValues = new ArrayList<>();
    return repeatParse(stream, parser, relatedValues);
  }

  public static CSSValue parseMaybe(
    CSSTokenStream stream, PropertyValueParser parser
  ) throws IOException {
    int mark = stream.mark();
    CSSValue value = parser.parse(stream);
    if (value.isFailure()) {
      stream.restoreMark(mark);
    } else {
      stream.discardMark();
    }

    return value;
  }

  public static record AnyOrderResult(CSSValue[] values) implements CSSValue {

    @Override
    public String serialize() {
      StringBuilder serialBuilder = new StringBuilder();
      for (CSSValue value: values) {
        if (value == null) continue;
        if (serialBuilder.length() != 0) {
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

  private static CSSValue repeatParse(
    CSSTokenStream stream,
    PropertyValueParser parser,
    List<CSSValue> relatedValues
  ) throws IOException {
    while (true) {
      int mark = stream.mark();
      CSSValue nextValue = parser.parse(stream);
      if (nextValue.isFailure()) {
        stream.restoreMark(mark);
        return ManyResult.createSpaces(relatedValues);
      }
      stream.discardMark();
      relatedValues.add(nextValue);
    }
  }

}
