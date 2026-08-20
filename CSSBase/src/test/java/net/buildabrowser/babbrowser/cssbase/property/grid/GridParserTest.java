package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class GridParserTest {
  
  private final GridParser gridParser = new GridParser();

  @Test
  @DisplayName("Can parse grid template value")
  public void canParseGridTemplateValue() throws IOException {
    CSSValue actual = gridParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("a a a"),
        StringToken.create("b b b")));
    
    CSSValue expectedTemplateAreas = GridTemplateAreasValue.create(List.of(
      GridArea.create("a", 1, 1, 3, 1),
      GridArea.create("b", 1, 2, 3, 1)));
    CSSValue expectedTemplateRows = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of(), CSSValue.AUTO),
        GridTrackValue.create(List.of(), CSSValue.AUTO)),
      null);
    CSSValue expectedTemplateColumns = CSSValue.NONE;
    CSSValue expected = GridValue.create(
      expectedTemplateRows, expectedTemplateColumns, expectedTemplateAreas,
      null, null, null);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse grid template rows then auto columns")
  public void canParseGridTemplateRowsThenAutoColumns() throws IOException {
    CSSValue actual = gridParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto"),
        DelimToken.create('/'),
        IdentToken.create("dense"),
        IdentToken.create("auto-flow"),
        IdentToken.create("min-content")));
    
    CSSValue expectedTemplateRows = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of(), CSSValue.AUTO)),
      null);
    CSSValue expectedAutoColumns = ManyResult.createSpaces(SizeValue.MIN_CONTENT);
    GridAutoFlowValue expectedAutoFlow = GridAutoFlowValue.create(
      GridAutoFlowDirection.COLUMN, true);
    CSSValue expected = GridValue.create(
      expectedTemplateRows, null, null,
      null, expectedAutoColumns, expectedAutoFlow);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse grid auto rows then templates columns")
  public void canParseGridAutoRowsThenTemplateColumns() throws IOException {
    CSSValue actual = gridParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto-flow"),
        IdentToken.create("auto"),
        DelimToken.create('/'),
        IdentToken.create("min-content")));
    
    CSSValue expectedAutoRows = ManyResult.createSpaces(CSSValue.AUTO);
    CSSValue expectedTemplateColumns = GridTrackListValue.create(
      List.of(
        GridTrackValue.create(List.of(), SizeValue.MIN_CONTENT)),
      null);
    GridAutoFlowValue expectedAutoFlow = GridAutoFlowValue.create(
      GridAutoFlowDirection.ROW, false);
    CSSValue expected = GridValue.create(
      null, expectedTemplateColumns, null,
      expectedAutoRows, null, expectedAutoFlow);
    Assertions.assertEquals(expected, actual);
  }

}
