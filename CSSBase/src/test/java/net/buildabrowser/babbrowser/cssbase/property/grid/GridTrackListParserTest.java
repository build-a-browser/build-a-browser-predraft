package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatNumberComponent;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTrackValue.GridRepeatValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class GridTrackListParserTest {

  private final GridTrackListParser gridTrackListParser
    = new GridTrackListParser(null);

  @Test
  @DisplayName("Can parse none value")
  public void canParseRowsAndColumnsValue() throws IOException {
    CSSValue actual = gridTrackListParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("none")));

    CSSValue expected = CSSValue.NONE;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single auto-sized track")
  public void canParseSingleAutoSizedTrack() throws IOException {
    CSSValue actual = gridTrackListParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto")));

    CSSValue expected = GridTrackListValue.create(List.of(
      GridTrackValue.create(List.of(), CSSValue.AUTO)
    ), null);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single auto-sized track with line names")
  public void canParseSingleAutoSizedTrackWidthLineNames() throws IOException {
    CSSValue actual = gridTrackListParser.parse(
      CSSTokenStream.createForTesting(
        DelimToken.create('['),
        IdentToken.create("fox"),
        IdentToken.create("kitsune"),
        DelimToken.create(']'),
        IdentToken.create("auto"),
        DelimToken.create('['),
        IdentToken.create("tail"),
        DelimToken.create(']')));

    CSSValue expected = GridTrackListValue.create(List.of(
      GridTrackValue.create(List.of("fox", "kitsune"), CSSValue.AUTO),
      GridTrackValue.create(List.of("tail"), null)
    ), null);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse two auto-sized tracks")
  public void canParseTwoAutoSizedTrack() throws IOException {
    CSSValue actual = gridTrackListParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto"),
        IdentToken.create("auto")));

    CSSValue expected = GridTrackListValue.create(List.of(
      GridTrackValue.create(List.of(), CSSValue.AUTO),
      GridTrackValue.create(List.of(), CSSValue.AUTO)
    ), null);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse repeat track")
  public void canParseRepeatTrack() throws IOException {
    CSSValue actual = gridTrackListParser.parse(
      CSSTokenStream.createForTesting(
        new FunctionValue("repeat", List.of(
          NumberToken.create(2),
          CommaToken.create(),
          DimensionToken.create(2, "fr")
        ))));

    CSSValue twoFr = LengthValue.create(2, true, LengthType.FR);
    CSSValue repeatValue = GridRepeatValue.create(
      GridRepeatNumberComponent.create(2),
      GridTrackListValue.create(List.of(
        GridTrackValue.create(List.of(), twoFr)
      ), null));
    CSSValue expected = GridTrackListValue.create(List.of(
      GridTrackValue.create(List.of(), repeatValue)
    ), null);
    Assertions.assertEquals(expected, actual);
  }

}
