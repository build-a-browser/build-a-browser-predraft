package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class FlexShrinkParserTest {
  
  private final FlexShrinkParser flexShrinkParser = new FlexShrinkParser();

  @Test
  @DisplayName("Can parse flex-shrink value")
  public void canParseFlexShrinkValue() throws IOException {
    CSSValue value = flexShrinkParser.parse(
      CSSTokenStream.create(NumberToken.create(7)));
    Assertions.assertEquals(FlexShrinkValue.create(7), value);
  }

}
