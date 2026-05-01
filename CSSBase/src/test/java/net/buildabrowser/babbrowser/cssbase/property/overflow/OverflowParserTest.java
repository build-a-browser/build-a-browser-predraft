package net.buildabrowser.babbrowser.cssbase.property.overflow;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class OverflowParserTest {
  
  private final OverflowParser overflowParser = new OverflowParser(null);
  
  @Test
  @DisplayName("Can parse overflow value")
  public void canParseOverflowValue() throws IOException {
    CSSValue value = overflowParser.parse(
      CSSTokenStream.create(IdentToken.create("scroll")));
    Assertions.assertEquals(OverflowValue.SCROLL, value);
  }

}
