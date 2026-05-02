package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class FlexGrowParserTest {
  
  private final FlexGrowParser flexGrowParser = new FlexGrowParser();

  @Test
  @DisplayName("Can parse flex-grow value")
  public void canParseFlexGrowValue() throws IOException {
    CSSValue value = flexGrowParser.parse(
      CSSTokenStream.createForTesting(NumberToken.create(7)));
    Assertions.assertEquals(FlexGrowValue.create(7), value);
  }

}
