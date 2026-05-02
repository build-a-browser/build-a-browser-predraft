package net.buildabrowser.babbrowser.cssbase.property.text;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class LineHeightParserTest {

  private static final LineHeightParser lineHeightParser = new LineHeightParser();

  @Test
  @DisplayName("Can parse normal line-height value")
  public void canParseNormalLineHeightValue() throws IOException {
    CSSValue value = lineHeightParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("normal")));
    Assertions.assertEquals(LineHeightValue.NORMAL, value);
  }
  
  @Test
  @DisplayName("Can parse length line-height value")
  public void canParseLengthSizeValue() throws IOException {
    CSSValue value = lineHeightParser.parse(
      CSSTokenStream.createForTesting(DimensionToken.create(4, "em")));
    Assertions.assertEquals(
      LengthValue.create(4, true, LengthType.EM),
      value);
  }

  @Test
  @DisplayName("Can parse percentage line-height value")
  public void canParsePercentageLineHeightValue() throws IOException {
    CSSValue value = lineHeightParser.parse(
      CSSTokenStream.createForTesting(PercentageToken.create(4)));
    Assertions.assertEquals(PercentageValue.create(4), value);
  }

  @Test
  @DisplayName("Can parse number line-height value")
  public void canParseNumberLineHeightValue() throws IOException {
    CSSValue value = lineHeightParser.parse(
      CSSTokenStream.createForTesting(NumberToken.create(4)));
    Assertions.assertEquals(LineHeightValue.NumberHeight.create(4), value);
  }

}
