package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class GridTemplateAreasParserTest {

  private final GridTemplateAreasParser gridTemplateAreasParser
    = new GridTemplateAreasParser();
  
  @Test
  @DisplayName("Can parse none value")
  public void canParseNoneValue() throws IOException {
    CSSValue actual = gridTemplateAreasParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("none")));

    CSSValue expected = CSSValue.NONE;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single row with single named cell")
  public void canParseSingleRowWithSingleNamedCell() throws IOException {
    CSSValue actual = gridTemplateAreasParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("fox")));

    CSSValue expected = GridTemplateAreasValue.create(List.of(
      GridArea.create("fox", 1, 1, 1, 1)));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single row with two named cells")
  public void canParseSingleRowWithTwoNamedCells() throws IOException {
    CSSValue actual = gridTemplateAreasParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("fox kit")));

    CSSValue expected = GridTemplateAreasValue.create(List.of(
      GridArea.create("fox", 1, 1, 1, 1),
      GridArea.create("kit", 2, 1, 1, 1)));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse two rows with single named cell")
  public void canParseTwoRowsWithSingleNamedCell() throws IOException {
    CSSValue actual = gridTemplateAreasParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("fox"),
        StringToken.create("kit")));

    CSSValue expected = GridTemplateAreasValue.create(List.of(
      GridArea.create("fox", 1, 1, 1, 1),
      GridArea.create("kit", 1, 2, 1, 1)));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single row with named cell and unnamed cell")
  public void canParseSingleRowWithNamedCellAndUnnamedCell() throws IOException {
    CSSValue actual = gridTemplateAreasParser.parse(
      CSSTokenStream.createForTesting(
        StringToken.create("kitsune ....")));

    CSSValue expected = GridTemplateAreasValue.create(List.of(
      GridArea.create("kitsune", 1, 1, 1, 1)));
    Assertions.assertEquals(expected, actual);
  }

}
