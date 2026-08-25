package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class GridLineCompositeParserTest {

  private final GridLineCompositeParser gridAreaParser
    = new GridLineCompositeParser(null, null, null);

  @Test
  @DisplayName("Can parse auto value")
  public void canParseAutoValue() throws IOException {
    CSSValue actual = gridAreaParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto")));

    CSSValue expected = new GridLineCompositeValue(List.of(
      CSSValue.AUTO, CSSValue.AUTO));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single custom ident value")
  public void canParseSingleCustomIdentValue() throws IOException {
    CSSValue actual = gridAreaParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("a")));

    CSSValue customIdent = customIdent("a");
    CSSValue expected = new GridLineCompositeValue(List.of(
      customIdent, customIdent));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse single nth line value")
  public void canParseSingleNthLineValue() throws IOException {
    CSSValue actual = gridAreaParser.parse(
      CSSTokenStream.createForTesting(
        NumberToken.create(5)));

    CSSValue lineNum = GridLineValue.create(
      false, false, 5, null);
    CSSValue expected = new GridLineCompositeValue(List.of(
      lineNum, CSSValue.AUTO));
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse double custom ident value")
  public void canParseDoubleCustomIdentValue() throws IOException {
    CSSValue actual = gridAreaParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("a"),
        DelimToken.create('/'),
        IdentToken.create("b")));

    CSSValue customIdent1 = customIdent("a");
    CSSValue customIdent2 = customIdent("b");
    CSSValue expected = new GridLineCompositeValue(List.of(
      customIdent1, customIdent2));
    Assertions.assertEquals(expected, actual);
  }

  private static CSSValue customIdent(String value) {
    return GridLineValue.create(
      false, true, 1, value);
  }
  
}
