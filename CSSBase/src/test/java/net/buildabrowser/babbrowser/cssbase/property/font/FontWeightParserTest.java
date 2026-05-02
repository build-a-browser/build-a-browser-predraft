package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightValue.RelativeFontWeightValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class FontWeightParserTest {
  
  private final FontWeightParser fontWeightParser = new FontWeightParser();

  @Test
  @DisplayName("Can parse named font weight")
  public void canParseNamedFontWeight() throws IOException {
    CSSValue value = fontWeightParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("bolder")));
    Assertions.assertEquals(RelativeFontWeightValue.BOLDER, value);
  }

  @Test
  @DisplayName("Can parse numerical font weight")
  public void canParseNumericalFontWeight() throws IOException {
    CSSValue value = fontWeightParser.parse(
      CSSTokenStream.createForTesting(NumberToken.create(500)));
    Assertions.assertEquals(FontWeightValue.create(500), value);
  }

}
