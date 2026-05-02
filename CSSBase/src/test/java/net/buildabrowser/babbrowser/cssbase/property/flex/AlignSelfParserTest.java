package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class AlignSelfParserTest {
 
  private final AlignSelfParser alignSelfParser = new AlignSelfParser();

  @Test
  @DisplayName("Can parse align-self value")
  public void canParseAlignSelfValue() throws IOException {
    CSSValue value = alignSelfParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("flex-start")));
    Assertions.assertEquals(AlignItemsValue.FLEX_START, value);
  }

  @Test
  @DisplayName("Can parse align-self value with auto")
  public void canParseAlignSelfValueWithAuto() throws IOException {
    CSSValue value = alignSelfParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("auto")));
    Assertions.assertEquals(CSSValue.AUTO, value);
  }

}
