package net.buildabrowser.babbrowser.cssbase.property.display;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class DisplayParserTest {

  private final DisplayParser displayParser = new DisplayParser();
  
  @Test
  @DisplayName("Can parse legacy display value")
  public void canParseLegacyDisplayValue() throws IOException {
    CSSValue value = displayParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("inline-block")));
    Assertions.assertEquals(
      DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW_ROOT),
      value);
  }

  @Test
  @DisplayName("Can parse outer-inner display value")
  public void canParseInnerDisplayValue() throws IOException {
    CSSValue value = displayParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("block"), IdentToken.create("flex")));
    Assertions.assertEquals(
      DisplayValue.create(OuterDisplayValue.BLOCK, InnerDisplayValue.FLEX),
      value);
  }

  @Test
  @DisplayName("Can parse display value with only one tuple half")
  public void canParseHalfDisplayValue() throws IOException {
    CSSValue value = displayParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("ruby")));
    Assertions.assertEquals(
      DisplayValue.create(OuterDisplayValue.INLINE, InnerDisplayValue.RUBY),
      value);
  }

}
