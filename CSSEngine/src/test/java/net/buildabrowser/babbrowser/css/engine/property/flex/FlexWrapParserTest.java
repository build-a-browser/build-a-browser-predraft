package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FlexWrapParserTest {
  
  private final FlexWrapParser flexWrapParser = new FlexWrapParser();

  @Test
  @DisplayName("Can parse flex-wrap value")
  public void canParseFlexWrapValue() throws IOException {
    CSSValue value = flexWrapParser.parse(
      CSSTokenStream.create(IdentToken.create("wrap-reverse")));
    Assertions.assertEquals(FlexWrapValue.WRAP_REVERSE, value);
  }

}
