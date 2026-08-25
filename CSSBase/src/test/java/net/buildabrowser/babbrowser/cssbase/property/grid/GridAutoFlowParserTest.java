package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GridAutoFlowParserTest {

  private final GridAutoFlowParser autoFlowParser = new GridAutoFlowParser();

  @Test
  @DisplayName("Can parse auto-flow direction")
  public void canParseAutoFlowDirection() throws IOException {
    CSSValue actual = autoFlowParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("column")));

    CSSValue expected = GridAutoFlowValue.create(
      GridAutoFlowDirection.COLUMN, false);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse just dense")
  public void canParseDense() throws IOException {
    CSSValue actual = autoFlowParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("dense")));

    CSSValue expected = GridAutoFlowValue.create(
      GridAutoFlowDirection.ROW, true);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse auto-flow direction then dense")
  public void canParseAutoFlowDirectionThenDense() throws IOException {
    CSSValue actual = autoFlowParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("column"),
        IdentToken.create("dense")));

    CSSValue expected = GridAutoFlowValue.create(
      GridAutoFlowDirection.COLUMN, true);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse dense then auto-flow direction")
  public void canParseDenseThenAutoFlowDirection() throws IOException {
    CSSValue actual = autoFlowParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("dense"),
        IdentToken.create("column")));

    CSSValue expected = GridAutoFlowValue.create(
      GridAutoFlowDirection.COLUMN, true);
    Assertions.assertEquals(expected, actual);
  }

}
