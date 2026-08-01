package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue.CustomIdentValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class GridLineParserTest {
  
  private final GridLineParser gridLineParser = new GridLineParser(null);

  @Test
  @DisplayName("Can parse auto value")
  public void canParseAutoValue() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto")));

    CSSValue expected = CSSValue.AUTO;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse custom ident value")
  public void canParseCustomIdentValue() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("pika")));

    CSSValue expected = CustomIdentValue.create("pika");
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse nth grid line")
  public void canParseNthGridLine() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        NumberToken.create(5)));

    CSSValue expected = GridLineValue.create(
      false, 5, null);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse nth named grid line")
  public void canParseNthNamedGridLine() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("pi"),
        NumberToken.create(6)));

    CSSValue expected = GridLineValue.create(
      false, 6, CustomIdentValue.create("pi"));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse reverse nth named grid line")
  public void canParseReverseNthNamedGridLine() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        NumberToken.create(-7),
        IdentToken.create("ria")));

    CSSValue expected = GridLineValue.create(
      false, -7, CustomIdentValue.create("ria"));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse span")
  public void canParseSpan() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("span"),
        NumberToken.create(3),
        IdentToken.create("hello")));

    CSSValue expected = GridLineValue.create(
      true, 3, CustomIdentValue.create("hello"));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse span with implied number")
  public void canParseSpanWithImpliedNumber() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("span"),
        IdentToken.create("world")));

    CSSValue expected = GridLineValue.create(
      true, 1, CustomIdentValue.create("world"));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse reverse span")
  public void canNotParseReverseSpan() throws IOException {
    CSSValue actual = gridLineParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("span"),
        NumberToken.create(-3),
        IdentToken.create("hello")));

    Assertions.assertTrue(actual.isFailure());
  }

}
