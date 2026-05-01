package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class FlexParserTest {
  
  private final FlexParser flexParser = new FlexParser();

  @Test
  @DisplayName("Can parse flex value just grow")
  public void canParseFlexValueWithGrow() throws IOException {
    CSSValue value = flexParser.parse(
      CSSTokenStream.create(NumberToken.create(2)));
    Assertions.assertEquals(
      FlexValue.create(
        FlexGrowValue.create(2),
        FlexShrinkValue.create(1),
        CSSValue.AUTO),
      value);
  }

  @Test
  @DisplayName("Can parse fully specified flex value")
  public void canParseFullySpecifiedFlexValue() throws IOException {
    CSSValue value = flexParser.parse(
      CSSTokenStream.create(
        PercentageToken.create(2),
        NumberToken.create(6),
        NumberToken.create(9)));
    Assertions.assertEquals(
      FlexValue.create(
        FlexGrowValue.create(6),
        FlexShrinkValue.create(9),
        PercentageValue.create(2)),
      value);
  }

  @Test
  @DisplayName("Can parse flex value with none")
  public void canParseFlexValueWithNone() throws IOException {
    CSSValue value = flexParser.parse(
      CSSTokenStream.create(IdentToken.create("none")));
    Assertions.assertEquals(
      FlexValue.create(
        FlexGrowValue.create(0),
        FlexShrinkValue.create(0),
        CSSValue.AUTO),
      value);
  }

  @Test
  @DisplayName("Can parse flex value with auto")
  public void canParseFlexValueWithAuto() throws IOException {
    CSSValue value = flexParser.parse(
      CSSTokenStream.create(IdentToken.create("auto")));
    Assertions.assertEquals(
      FlexValue.create(
        FlexGrowValue.create(1),
        FlexShrinkValue.create(1),
        CSSValue.AUTO),
      value);
  }

}
