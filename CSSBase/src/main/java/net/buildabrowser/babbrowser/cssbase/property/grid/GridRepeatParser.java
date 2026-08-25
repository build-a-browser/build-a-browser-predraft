package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNameComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNumberComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class GridRepeatParser {

  private final GridTrackListParser gridTrackListParser;

  public GridRepeatParser(
    GridTrackListParser gridTrackListParser
  ) {
    this.gridTrackListParser = gridTrackListParser;
  }

  public CSSValue parseTrackRepeat(
    CSSTokenStream stream
  ) throws IOException {
    return parseRepeatFunction(
      stream,
      true, false, false,
      RepeatSizeMode.TRACK);
  }

  public CSSValue parseAutoRepeat(
    CSSTokenStream stream
  ) throws IOException {
    return parseRepeatFunction(
      stream,
      false, true, true,
      RepeatSizeMode.FIXED);
  }

  public CSSValue parseFixedRepeat(
    CSSTokenStream stream
  ) throws IOException {
    return parseRepeatFunction(
      stream,
      true, false, false,
      RepeatSizeMode.FIXED);
  }

  public CSSValue parseNameRepeat(
    CSSTokenStream stream
  ) throws IOException {
    return parseRepeatFunction(
      stream,
      true, true, false,
      RepeatSizeMode.NONE);
  }

  private CSSValue parseRepeatFunction(
    CSSTokenStream stream,
    boolean allowInt,
    boolean allowAutoFill,
    boolean allowAutoFit,
    RepeatSizeMode sizeMode
  ) throws IOException {
    Token firstToken = stream.read();
    CSSValue repeatTimesValue = null;
    if (
      allowInt
      && firstToken instanceof NumberToken numberToken
      && numberToken.isInteger()
      && numberToken.value().intValue() >= 1
    ) {
      repeatTimesValue = GridRepeatNumberComponent.create(numberToken.value().intValue());
    } else if (
      allowAutoFill
      && firstToken instanceof IdentToken identToken
      && identToken.value().equals("auto-fill")
    ) {
      repeatTimesValue = GridRepeatNameComponent.AUTO_FILL;
    } else if (
      allowAutoFill
      && firstToken instanceof IdentToken identToken
      && identToken.value().equals("auto-fit")
    ) {
      repeatTimesValue = GridRepeatNameComponent.AUTO_FIT;
    } else {
      return allowInt ?
        CSSFailure.EXPECTED_INTEGER :
        CSSFailure.EXPECTED_IDENT;
    }

    if (!(stream.read() instanceof CommaToken)) {
      return CSSFailure.EXPECTED_COMMA;
    }

    CSSValue trackList = switch (sizeMode) {
      case TRACK -> gridTrackListParser.parseTrackList(stream, false);
      case FIXED -> gridTrackListParser.parseAutoTrackList(stream, false);
      case NONE -> gridTrackListParser.parseLineNamesTrackList(stream);
      default -> throw new UnsupportedOperationException(
        "Unrecognized size mode: " + sizeMode);
    };

    if (trackList.isFailure()) return trackList;

    return GridRepeatValue.create(repeatTimesValue, (GridTrackListValue) trackList);
  }


  private static enum RepeatSizeMode {
    TRACK, FIXED, NONE;
  }
  
}
