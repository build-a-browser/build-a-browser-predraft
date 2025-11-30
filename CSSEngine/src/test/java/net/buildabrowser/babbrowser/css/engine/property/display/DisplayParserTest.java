package net.buildabrowser.babbrowser.css.engine.property.display;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.DisplayUnionValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class DisplayParserTest {

  private final DisplayParser displayParser = new DisplayParser();
  
  @Test
  @DisplayName("Can parse legacy display value")
  public void canParseLegacyDisplayValue() throws IOException {
    CSSValue value = displayParser.parse(
      CSSTokenStream.create(IdentToken.create("inline-block")),
      ActiveStyles.create());
    Assertions.assertEquals(
      new DisplayUnionValue(OuterDisplayValue.INLINE, InnerDisplayValue.FLOW_ROOT),
      value);
  }

  @Test
  @DisplayName("Can parse outer-inner display value")
  public void canParseInnerDisplayValue() throws IOException {
    CSSValue value = displayParser.parse(
      CSSTokenStream.create(IdentToken.create("block"), IdentToken.create("flex")),
      ActiveStyles.create());
    Assertions.assertEquals(
      new DisplayUnionValue(OuterDisplayValue.BLOCK, InnerDisplayValue.FLEX),
      value);
  }

  @Test
  @DisplayName("Can parse display value with only one tuple half")
  public void canParseHalfDisplayValue() throws IOException {
    CSSValue value = displayParser.parse(
      CSSTokenStream.create(IdentToken.create("ruby")),
      ActiveStyles.create());
    Assertions.assertEquals(
      new DisplayUnionValue(OuterDisplayValue.INLINE, InnerDisplayValue.RUBY),
      value);
  }

}
