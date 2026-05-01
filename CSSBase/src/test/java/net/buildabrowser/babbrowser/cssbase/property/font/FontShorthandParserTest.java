package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightValue.RelativeFontWeightValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class FontShorthandParserTest {
  
  private final FontShorthandParser fontShorthandParser = new FontShorthandParser();

  @Test
  @DisplayName("Can parse minimal font shorthand")
  public void canParseMinimalFontShorthand() throws IOException {
    CSSValue value = fontShorthandParser.parse(CSSTokenStream.create(
      DimensionToken.create(4, "px"),
      IdentToken.create("monospace")));
    Assertions.assertEquals(
      new FontShorthandValue(
        null,
        LengthValue.create(4, true, LengthType.PX),
        null,
        new ManyResult(List.of(FontFamilyValue.MONOSPACE))),
      value);
  }

  @Test
  @DisplayName("Can parse maximal font shorthand")
  public void canParseMaximalFontShorthand() throws IOException {
    CSSValue value = fontShorthandParser.parse(CSSTokenStream.create(
      IdentToken.create("bolder"),
      DimensionToken.create(4, "px"),
      DelimToken.create('/'),
      DimensionToken.create(2, "em"),
      StringToken.create("arial"),
      new CommaToken(),
      IdentToken.create("serif")));
    Assertions.assertEquals(
      new FontShorthandValue(
        RelativeFontWeightValue.BOLDER,
        LengthValue.create(4, true, LengthType.PX),
        LengthValue.create(2, true, LengthType.EM),
        new ManyResult(List.of(FontNameValue.create("arial"), FontFamilyValue.SERIF))),
      value);
  }
  
}
