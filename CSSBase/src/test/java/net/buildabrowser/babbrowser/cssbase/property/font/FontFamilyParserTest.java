package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class FontFamilyParserTest {
  
  private final FontFamilyParser fontFamilyParser = new FontFamilyParser();

  @Test
  @DisplayName("Can parse font name")
  public void canParseFontName() throws IOException {
    CSSValue value = fontFamilyParser.parse(
      CSSTokenStream.createForTesting(StringToken.create("arial")));
    Assertions.assertEquals(
      new ManyResult(List.of(FontNameValue.create("arial"))),
      value);
  }

  @Test
  @DisplayName("Can parse font family")
  public void canParseFontFamily() throws IOException {
    CSSValue value = fontFamilyParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("monospace")));
    Assertions.assertEquals(
      new ManyResult(List.of(FontFamilyValue.MONOSPACE)),
      value);
  }

  @Test
  @DisplayName("Can parse font-family with fallbacks")
  public void canParseFontFamilyWithFallbacks() throws IOException {
    CSSValue value = fontFamilyParser.parse(
      CSSTokenStream.createForTesting(StringToken.create("helvetica"),
      CommaToken.create(),
      IdentToken.create("Arial"),
      CommaToken.create(),
      StringToken.create("sans-serif"),
      CommaToken.create(),
      IdentToken.create("sans-serif")));
    Assertions.assertEquals(
      new ManyResult(List.of(
        FontNameValue.create("helvetica"),
        FontNameValue.create("Arial"),
        FontNameValue.create("sans-serif"),
        FontFamilyValue.SANS_SERIF)),
      value);
  }

}
