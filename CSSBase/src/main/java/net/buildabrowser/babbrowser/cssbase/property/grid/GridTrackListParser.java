package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GridTrackListParser implements PropertyValueParser {

  private static final CSSValue EXPECTED_LS_BRACKET = new CSSFailure(
    "Expected a left square bracket!");
  private static final CSSValue NOT_IMPLEMENTED = new CSSFailure(
    "Not implemented!");

  private final GridTrackSizeParser trackSizeParser;
  private final GridRepeatParser gridRepeatParser;

  private final CSSProperty relatedProperty;

  public GridTrackListParser(
    CSSProperty relatedProperty
  ) {
    this.relatedProperty = relatedProperty;
    this.trackSizeParser = new GridTrackSizeParser(true);
    this.gridRepeatParser = new GridRepeatParser(this);
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      stream.read();
      return CSSValue.NONE;
    } else if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("subgrid")
    ) {
      stream.read();
      return parseSubgridLineNameList(stream);
    }

    int position = stream.position();
    CSSValue result = parseAutoTrackList(stream, true);
    if (!result.isFailure()) return result;

    stream.seek(position);
    
    return parseTrackList(stream, true);
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }

  private CSSValue parseSubgridLineNameList(SeekableCSSTokenStream stream) {
    return NOT_IMPLEMENTED;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public CSSValue parseTrackList(
    SeekableCSSTokenStream stream,
    boolean allowRepeat
  ) throws IOException {
    CSSValue value = PropertyValueParserUtil.parseOneOrMore(
      stream, s -> parseTrackValue(s, allowRepeat));
    if (value.isFailure()) return value;
    // Relies on parseOneOrMore using a mutable list
    List<GridTrackValue> trackList = (List) ((ManyResult) value).values();

    List<String> lineNames = new ArrayList<>(1);
    CSSValue errValue = parseLineNames(stream, lineNames);
    if (errValue != null) return errValue;
    if (!lineNames.isEmpty()) {
      trackList.add(GridTrackValue.create(lineNames, null));
    }
    
    return GridTrackListValue.create(trackList, null);
  }

  private CSSValue parseTrackValue(
    SeekableCSSTokenStream stream,
    boolean allowRepeat
  ) throws IOException {
    List<String> lineNames = new ArrayList<>(1);
    CSSValue errValue = parseLineNames(stream, lineNames);
    if (errValue != null) return errValue;

    boolean isRepeat =
      allowRepeat
      && stream.peek() instanceof FunctionValue functionValue
      && functionValue.name().equals("repeat");
    CSSValue sizeOrRepeat = isRepeat ?
      gridRepeatParser.parseTrackRepeat(streamFunctionValue(stream)) :
      trackSizeParser.parseTrackSize(stream);
    if (sizeOrRepeat.isFailure()) return sizeOrRepeat;
    
    return GridTrackValue.create(lineNames, sizeOrRepeat);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public CSSValue parseAutoTrackList(
    SeekableCSSTokenStream stream,
    boolean allowRepeat
  ) throws IOException {
    CSSValue value = PropertyValueParserUtil.parseZeroOrMore(
      stream, s -> this.parseAutoTrackValue(s, allowRepeat));
    if (value.isFailure()) return value;
    // Relies on parseOneOrMore using a mutable list
    List<GridTrackValue> trackList = (List) ((ManyResult) value).values();

    List<String> lineNames = new ArrayList<>(1);
    CSSValue errValue = parseLineNames(stream, lineNames);
    if (errValue != null) return errValue;
    if (!lineNames.isEmpty()) {
      trackList.add(GridTrackValue.create(lineNames, null));
    }

    boolean isRepeat =
      allowRepeat
      && stream.peek() instanceof FunctionValue functionValue
      && functionValue.name().equals("repeat");
    CSSValue maybeRepeat = isRepeat ?
      gridRepeatParser.parseAutoRepeat(streamFunctionValue(stream)) :
      null;
    if (
      maybeRepeat != null
      && maybeRepeat.isFailure()
    ) return maybeRepeat;
    
    return GridTrackListValue.create(trackList, maybeRepeat);
  }

  private CSSValue parseAutoTrackValue(
    SeekableCSSTokenStream stream,
    boolean allowRepeat
  ) throws IOException {
    List<String> lineNames = new ArrayList<>(1);
    CSSValue errValue = parseLineNames(stream, lineNames);
    if (errValue != null) return errValue;

    boolean isRepeat =
      allowRepeat
      && stream.peek() instanceof FunctionValue functionValue
      && functionValue.name().equals("repeat");
    CSSValue sizeOrRepeat = isRepeat ?
      gridRepeatParser.parseTrackRepeat(streamFunctionValue(stream)) :
      trackSizeParser.parseTrackSize(stream);
    if (sizeOrRepeat.isFailure()) return sizeOrRepeat;
    
    return GridTrackValue.create(lineNames, sizeOrRepeat);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public CSSValue parseLineNamesTrackList(SeekableCSSTokenStream stream) throws IOException {
    CSSValue value = PropertyValueParserUtil.parseOneOrMore(
      stream, this::parseLineNamesTrackValue);
    if (value.isFailure()) return value;
    List<GridTrackValue> trackList = (List) ((ManyResult) value).values();
    
    return GridTrackListValue.create(trackList, null);
  }

  private CSSValue parseLineNamesTrackValue(SeekableCSSTokenStream stream) throws IOException {
    if (!(
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '['
    )) return EXPECTED_LS_BRACKET;
    
    List<String> lineNames = new ArrayList<>(1);
    CSSValue errValue = parseLineNames(stream, lineNames);
    if (errValue != null) return errValue;
    
    return GridTrackValue.create(lineNames, null);
  }

  public CSSValue parseLineNames(
    SeekableCSSTokenStream stream, List<String> lineNames
  ) throws IOException {

    if (!(
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '['
    )) return null;
    stream.read();

    while (!(
      stream.peek() instanceof DelimToken delimToken2
      && delimToken2.ch() == ']'
    )) {
      CSSValue errVal = parseLineName(stream, lineNames);
      if (errVal != null) return errVal;
    }
    stream.read();

    return null;
  }

  private CSSValue parseLineName(
    SeekableCSSTokenStream stream, List<String> lineNames
  ) throws IOException {
    if (!(
      stream.read() instanceof IdentToken identToken
    )) return CSSFailure.EXPECTED_IDENT;

    lineNames.add(identToken.value());
    return null;
  }

  private SeekableCSSTokenStream streamFunctionValue(
    SeekableCSSTokenStream stream
  ) throws IOException {
    FunctionValue functionValue = (FunctionValue) stream.read();
    return ListCSSTokenStream.createWithSkippedWhitespace(
      stream.source(),
      functionValue.value());
  }
  
}
