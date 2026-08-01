package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GridTrackSizeParserTest {
  
  private final GridTrackSizeParser trackSizeParser = new GridTrackSizeParser(false);
  private final GridTrackSizeParser fixedSizeParser = new GridTrackSizeParser(true);

  @Test
  @DisplayName("Can parse auto track size")
  public void canParseAutoTrackSize() throws IOException {
    CSSValue actual = trackSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto")));

    CSSValue expected = CSSValue.AUTO;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse simple track size")
  public void canParseSimpleTrackSize() throws IOException {
    CSSValue actual = trackSizeParser.parse(
      CSSTokenStream.createForTesting(
        DimensionToken.create(5, "px")));

    CSSValue expected = LengthValue.create(5, true, LengthType.PX);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse min-content size (not fixed)")
  public void canParseMinContentSize() throws IOException {
    CSSValue actual = trackSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("min-content")));

    CSSValue expected = SizeValue.MIN_CONTENT;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse min-content size (fixed)")
  public void canNotParseMinContentSizeFixed() throws IOException {
    CSSValue actual = fixedSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("min-content")));

    Assertions.assertTrue(actual.isFailure());
  }

  @Test
  @DisplayName("Can parse flexible size (not fixed)")
  public void canParseFlexibleSize() throws IOException {
    CSSValue actual = trackSizeParser.parse(
      CSSTokenStream.createForTesting(
        DimensionToken.create(5, "fr")));

    CSSValue expected = LengthValue.create(5, true, LengthType.FR);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse flexible size (fixed)")
  public void canNotParseFlexibleSizeFixed() throws IOException {
    CSSValue actual = fixedSizeParser.parse(
      CSSTokenStream.createForTesting(
        DimensionToken.create(5, "fr")));

    Assertions.assertTrue(actual.isFailure());
  }

  @Test
  @DisplayName("Can parse minmax inflexible track (not fixed)")
  public void canParseMinMaxInflexibleTrack() throws IOException {
    CSSValue actual = trackSizeParser.parse(
      CSSTokenStream.createForTesting(
        new FunctionValue("minmax", List.of(
          IdentToken.create("max-content"),
          CommaToken.create(),
          DimensionToken.create(5, "fr")))));

    CSSValue expected = GridMinMaxValue.create(
      SizeValue.MAX_CONTENT,
      LengthValue.create(5, true, LengthType.FR));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse minmax inflexible track (fixed)")
  public void canNotParseMinMaxInflexibleTrackFixed() throws IOException {
    CSSValue actual = fixedSizeParser.parse(
      CSSTokenStream.createForTesting(
        new FunctionValue("minmax", List.of(
          IdentToken.create("max-content"),
          CommaToken.create(),
          DimensionToken.create(5, "fr")))));

    Assertions.assertTrue(actual.isFailure());
  }

  @Test
  @DisplayName("Can parse minmax fixed track (fixed)")
  public void canParseMinMaxFixedTrackFixed() throws IOException {
    CSSValue actual = fixedSizeParser.parse(
      CSSTokenStream.createForTesting(
        new FunctionValue("minmax", List.of(
          DimensionToken.create(6, "px"),
          CommaToken.create(),
          DimensionToken.create(5, "fr")))));

    CSSValue expected = GridMinMaxValue.create(
      LengthValue.create(6, true, LengthType.PX),
      LengthValue.create(5, true, LengthType.FR));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse minmax inflexible fixed (fixed)")
  public void canParseMinMaxInflexibleFixedFixed() throws IOException {
    CSSValue actual = fixedSizeParser.parse(
      CSSTokenStream.createForTesting(
        new FunctionValue("minmax", List.of(
          IdentToken.create("min-content"),
          CommaToken.create(),
          DimensionToken.create(6, "px")))));

    CSSValue expected = GridMinMaxValue.create(
      SizeValue.MIN_CONTENT,
      LengthValue.create(6, true, LengthType.PX));
    Assertions.assertEquals(expected, actual);
  }

}
