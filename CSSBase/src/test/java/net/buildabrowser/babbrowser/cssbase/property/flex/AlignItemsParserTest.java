package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class AlignItemsParserTest {
 
  private final AlignItemsParser alignItemsParser = new AlignItemsParser();

  @Test
  @DisplayName("Can parse align-items value")
  public void canParseAlignItemsValue() throws IOException {
    CSSValue value = alignItemsParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("flex-end")));
    Assertions.assertEquals(AlignItemsValue.FLEX_END, value);
  }

}
