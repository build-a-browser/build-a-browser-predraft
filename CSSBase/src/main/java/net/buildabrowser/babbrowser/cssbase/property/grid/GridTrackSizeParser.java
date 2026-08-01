package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class GridTrackSizeParser implements PropertyValueParser {

  private final SizeParser sizeParserTrack = new SizeParser(
    false, true, true, true, null);
  private final SizeParser sizeParserFixed = new SizeParser(
    false, false, true, false, null);
  private final SizeParser sizeParserOuter = new SizeParser(
    false, true, true, true, null);

  private final boolean isFixed;

  public GridTrackSizeParser(boolean isFixed) {
    this.isFixed = isFixed;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return isFixed ?
      parseFixedSize(stream) :
      parseTrackSize(stream);
  }

  public CSSValue parseTrackSize(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof FunctionValue functionValue
      && functionValue.name().equals("minmax")
    ) {
      stream.read();
      SeekableCSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
        stream.source(), functionValue.value());
      return parseMinMax(
        innerStream,
        this::parseInflexibleBreadth,
        this::parseTrackBreadth);
    }

    return parseTrackBreadthShared(stream, sizeParserOuter, true);
  }

  public CSSValue parseFixedSize(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof FunctionValue functionValue
      && functionValue.name().equals("minmax")
    ) {
      stream.read();
      SeekableCSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
        stream.source(), functionValue.value());

      CSSValue result = parseMinMax(
        innerStream,
        this::parseFixedBreadth,
        this::parseTrackBreadth);
      if (!result.isFailure()) {
        return result;
      }

      innerStream.seek(0);
      return parseMinMax(
        innerStream,
        this::parseInflexibleBreadth,
        this::parseFixedBreadth);
    }
    
    return parseTrackBreadthShared(stream, sizeParserFixed, false);
  }

  private CSSValue parseTrackBreadth(SeekableCSSTokenStream stream) throws IOException {
    return parseTrackBreadthShared(stream, sizeParserTrack, true);
  }

  private CSSValue parseInflexibleBreadth(SeekableCSSTokenStream stream) throws IOException {
    return parseTrackBreadthShared(stream, sizeParserTrack, false);
  }

  private CSSValue parseFixedBreadth(SeekableCSSTokenStream stream) throws IOException {
    return parseTrackBreadthShared(stream, sizeParserFixed, false);
  }

  private CSSValue parseTrackBreadthShared(
    SeekableCSSTokenStream stream,
    SizeParser sizeParser,
    boolean isFlexible
  ) throws IOException {
    if (
      isFlexible
      && stream.peek() instanceof DimensionToken dimensionToken
      && dimensionToken.dimension().equals("fr")
      && dimensionToken.value().floatValue() >= 0f
    ) {
      stream.read();
      return LengthValue.create(
        dimensionToken.value(),
        dimensionToken.isInteger(),
        LengthType.FR);
    }
    return sizeParser.parse(stream);
  }

  private CSSValue parseMinMax(
    SeekableCSSTokenStream stream,
    PropertyValueParser minParser,
    PropertyValueParser maxParser
  ) throws IOException {
    CSSValue min = minParser.parse(stream);
    if (min.isFailure()) return min;

    if (!(
      stream.read() instanceof CommaToken
    )) return CSSFailure.EXPECTED_COMMA;

    CSSValue max = maxParser.parse(stream);
    if (max.isFailure()) return max;

    if (!(
      stream.peek() instanceof EOFToken
    )) return CSSFailure.EXPECTED_EOF;

    return GridMinMaxValue.create(min, max);
  }
  
}
