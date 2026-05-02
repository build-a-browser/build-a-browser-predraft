package net.buildabrowser.babbrowser.cssbase.property.align;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GapShorthandParserTest {

  private final GapShorthandParser gapShorthandParser = new GapShorthandParser();
  
  @Test
  @DisplayName("Can parse gap shorthand value with one element")
  public void canParseGapShorthandValueWithOneElement() throws IOException {
    CSSValue value = gapShorthandParser.parse(
      CSSTokenStream.createForTesting(DimensionToken.create(4, "em")));
    Assertions.assertEquals(
      new GapShorthandValue(
        LengthValue.create(4, true, LengthType.EM),
        LengthValue.create(4, true, LengthType.EM)),
      value);
  }

  @Test
  @DisplayName("Can parse gap shorthand value with two elements")
  public void canParseGapShorthandValueWithTwoElements() throws IOException {
    CSSValue value = gapShorthandParser.parse(
      CSSTokenStream.createForTesting(DimensionToken.create(4, "em"),
      IdentToken.create("normal")));
    Assertions.assertEquals(
      new GapShorthandValue(
        LengthValue.create(4, true, LengthType.EM),
        GapValue.NORMAL),
      value);
  }

}
