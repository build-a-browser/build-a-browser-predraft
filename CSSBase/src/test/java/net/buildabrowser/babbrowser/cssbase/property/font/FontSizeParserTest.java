package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FontSizeParserTest {
  
  private final FontSizeParser fontSizeParser = new FontSizeParser();

  @Test
  @DisplayName("Can parse named font size")
  public void canParseNamedFontSize() throws IOException {
    CSSValue value = fontSizeParser.parse(
      CSSTokenStream.create(IdentToken.create("xxx-large")));
    Assertions.assertEquals(FontNamedSizeValue.XXX_LARGE, value);
  }

  @Test
  @DisplayName("Can parse relative font size")
  public void canParseRelativeFontSize() throws IOException {
    CSSValue value = fontSizeParser.parse(
      CSSTokenStream.create(DimensionToken.create(2, "em")));
    Assertions.assertEquals(
      LengthValue.create(2, true, LengthType.EM),
      value);
  }

}
