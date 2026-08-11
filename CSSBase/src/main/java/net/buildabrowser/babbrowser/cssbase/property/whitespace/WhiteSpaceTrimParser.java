package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceTrimValue.WhiteSpaceTrimComponent;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class WhiteSpaceTrimParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_TRIM_FAILURE = new CSSFailure(
    "Expected a valid white-space-trim value!");

  private final PropertyValueParser[] parsers = new PropertyValueParser[] {
    s -> parseInner(s,
      "discard-before", WhiteSpaceTrimComponent.DISCARD_BEFORE),
    s -> parseInner(s,
      "discard-after", WhiteSpaceTrimComponent.DISCARD_AFTER),
    s -> parseInner(s,
      "discard-inner", WhiteSpaceTrimComponent.DISCARD_INNER)
  };

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) return WhiteSpaceTrimValue.NONE;

    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream, parsers);
    if (result.isFailure()) return result;
    CSSValue[] anyOrderValues = ((AnyOrderResult) result).values();

    return new WhiteSpaceTrimValue(
      anyOrderValues[0] != null,
      anyOrderValues[1] != null,
      anyOrderValues[2] != null);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.WHITE_SPACE_TRIM;
  }

  private CSSValue parseInner(
    SeekableCSSTokenStream stream,
    String targetName,
    CSSValue targetValue
  ) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals(targetName)
    ) {
      return targetValue;
    }

    return EXPECTED_TRIM_FAILURE;
  }

}
