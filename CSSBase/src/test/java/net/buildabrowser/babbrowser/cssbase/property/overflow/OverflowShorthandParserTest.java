package net.buildabrowser.babbrowser.cssbase.property.overflow;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class OverflowShorthandParserTest {
  
  private final OverflowShorthandParser overflowShorthandParser = new OverflowShorthandParser();
  
  @Test
  @DisplayName("Can parse overflow value with one component")
  public void canParseOverflowValueWithOneComponent() throws IOException {
    CSSValue value = overflowShorthandParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("scroll")));
    Assertions.assertEquals(
      OverflowShorthandValue.create(
        OverflowValue.SCROLL, OverflowValue.SCROLL
      ), value);
  }
  
  @Test
  @DisplayName("Can parse overflow value with two components")
  public void canParseOverflowValueWithTwoComponents() throws IOException {
    CSSValue value = overflowShorthandParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("clip"),
        IdentToken.create("hidden")));
    Assertions.assertEquals(
      OverflowShorthandValue.create(
        OverflowValue.CLIP, OverflowValue.HIDDEN
      ), value);
  }

}
