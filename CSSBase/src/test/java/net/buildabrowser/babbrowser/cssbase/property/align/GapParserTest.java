package net.buildabrowser.babbrowser.cssbase.property.align;

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
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class GapParserTest {
  
  private static final GapParser gapParser = new GapParser(null);

  @Test
  @DisplayName("Can parse normal gap value")
  public void canParseNormalGapValue() throws IOException {
    CSSValue value = gapParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("normal")));
    Assertions.assertEquals(GapValue.NORMAL, value);
  }
  
  @Test
  @DisplayName("Can parse length gap value")
  public void canParseLengthSizeValue() throws IOException {
    CSSValue value = gapParser.parse(
      CSSTokenStream.createForTesting(DimensionToken.create(4, "em")));
    Assertions.assertEquals(
      LengthValue.create(4, true, LengthType.EM),
      value);
  }

  @Test
  @DisplayName("Can parse percentage gap value")
  public void canParsePercentageGapValue() throws IOException {
    CSSValue value = gapParser.parse(
      CSSTokenStream.createForTesting(PercentageToken.create(4)));
    Assertions.assertEquals(PercentageValue.create(4), value);
  }

}
