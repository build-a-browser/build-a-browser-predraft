package net.buildabrowser.babbrowser.css.engine.property.floats;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FloatParserTest {

  private final FloatParser floatsParser = new FloatParser();
  
  @Test
  @DisplayName("Can parse float value")
  public void canParseFloatValue() throws IOException {
    CSSValue value = floatsParser.parse(
      CSSTokenStream.create(IdentToken.create("left")));
    Assertions.assertEquals(FloatValue.LEFT, value);
  }

}
