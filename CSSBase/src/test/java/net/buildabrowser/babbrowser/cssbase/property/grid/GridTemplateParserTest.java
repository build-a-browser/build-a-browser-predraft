package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridTemplateAreasRowValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class GridTemplateParserTest {
  
  private final GridTemplateParser gridTemplateParser = new GridTemplateParser();

  @Test
  @DisplayName("Can parse none value")
  public void canParseNoneValue() throws IOException {
    CSSValue actual = gridTemplateParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("none")));

    CSSValue expected = GridTemplateValue.NONE;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse rows / columns value")
  public void canParseRowsAndColumnsValue() throws IOException {
    CSSValue actual = gridTemplateParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto"),
        DimensionToken.create(1, "fr"),
        DelimToken.create('/'),
        IdentToken.create("auto"),
        DimensionToken.create(1, "fr"),
        IdentToken.create("auto")));

    CSSValue oneFr = LengthValue.create(1, true, LengthType.FR);
    GridTrackValue trackAuto = GridTrackValue.create(List.of(), CSSValue.AUTO);
    GridTrackValue trackOneFr = GridTrackValue.create(List.of(), oneFr);
    CSSValue expected = GridTemplateValue.create(
      GridTrackListValue.create(List.of(trackAuto, trackOneFr), null),
      GridTrackListValue.create(List.of(trackAuto, trackOneFr, trackAuto), null),
      CSSValue.NONE);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse minimal value including template areas")
  public void canParseValueWithTemplateAreas() throws IOException {
    CSSValue actual = gridTemplateParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("a a a"),
        StringToken.create("b b b")));
    
    CSSValue expectedTemplateAreas = GridTemplateAreasValue.create(List.of(
      GridTemplateAreasRowValue.create(List.of("a", "a", "a")),
      GridTemplateAreasRowValue.create(List.of("b", "b", "b"))));
    CSSValue expectedTemplateRows = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of(), CSSValue.AUTO),
        GridTrackValue.create(List.of(), CSSValue.AUTO)),
      null);
    CSSValue expectedTemplateColumns = CSSValue.NONE;
    CSSValue expected = GridTemplateValue.create(
      expectedTemplateRows, expectedTemplateColumns, expectedTemplateAreas);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse value with template areas and track sizes")
  public void canParseValueWithTemplateAreasAndTrackSizes() throws IOException {
    CSSValue actual = gridTemplateParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("a a a"),
        StringToken.create("b b b"),
        DimensionToken.create(1, "fr")));
    
    CSSValue expectedTemplateAreas = GridTemplateAreasValue.create(List.of(
      GridTemplateAreasRowValue.create(List.of("a", "a", "a")),
      GridTemplateAreasRowValue.create(List.of("b", "b", "b"))));
    CSSValue expectedTemplateRows = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of(), CSSValue.AUTO),
        GridTrackValue.create(List.of(), LengthValue.create(1, LengthType.FR))),
      null);
    CSSValue expectedTemplateColumns = CSSValue.NONE;
    CSSValue expected = GridTemplateValue.create(
      expectedTemplateRows, expectedTemplateColumns, expectedTemplateAreas);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse value with template areas and line names")
  public void canParseValueWithTemplateAreasAndLineNames() throws IOException {
    CSSValue actual = gridTemplateParser.parse(
      CSSTokenStream.createForTesting(
        DelimToken.create('['),
        IdentToken.create("header-top"),
        DelimToken.create(']'),
        StringToken.create("a a a"),
        DelimToken.create('['),
        IdentToken.create("header-bottom"),
        IdentToken.create("two-item-test"),
        DelimToken.create(']'),
        DelimToken.create('['),
        IdentToken.create("main-top"),
        DelimToken.create(']'),
        StringToken.create("b b b"),
        DelimToken.create('['),
        IdentToken.create("main-bottom"),
        DelimToken.create(']')
      ));
    
    CSSValue expectedTemplateAreas = GridTemplateAreasValue.create(List.of(
      GridTemplateAreasRowValue.create(List.of("a", "a", "a")),
      GridTemplateAreasRowValue.create(List.of("b", "b", "b"))));
    CSSValue expectedTemplateRows = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of("header-top"), CSSValue.AUTO),
        GridTrackValue.create(List.of(
          "header-bottom", "two-item-test", "main-top"
        ), CSSValue.AUTO),
        GridTrackValue.create(List.of(
          "main-bottom"
        ), null)),
      null);
    CSSValue expectedTemplateColumns = CSSValue.NONE;
    CSSValue expected = GridTemplateValue.create(
      expectedTemplateRows, expectedTemplateColumns, expectedTemplateAreas);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse value with template areas and template columns")
  public void canParseValueWithTemplateAreasAndTemplaceColumns() throws IOException {
    CSSValue actual = gridTemplateParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("a a a"),
        StringToken.create("b b b"),
        DelimToken.create('/'),
        IdentToken.create("auto"),
        DimensionToken.create(1, "fr"),
        IdentToken.create("auto")));
    
    CSSValue oneFr = LengthValue.create(1, true, LengthType.FR);
    GridTrackValue trackAuto = GridTrackValue.create(List.of(), CSSValue.AUTO);
    GridTrackValue trackOneFr = GridTrackValue.create(List.of(), oneFr);

    CSSValue expectedTemplateAreas = GridTemplateAreasValue.create(List.of(
      GridTemplateAreasRowValue.create(List.of("a", "a", "a")),
      GridTemplateAreasRowValue.create(List.of("b", "b", "b"))));
    CSSValue expectedTemplateRows = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of(), CSSValue.AUTO),
        GridTrackValue.create(List.of(), CSSValue.AUTO)),
      null);
    CSSValue expectedTemplateColumns = GridTrackListValue.create(
      List.of(trackAuto, trackOneFr, trackAuto), null);
    CSSValue expected = GridTemplateValue.create(
      expectedTemplateRows, expectedTemplateColumns, expectedTemplateAreas);
    Assertions.assertEquals(expected, actual);
  }

}
