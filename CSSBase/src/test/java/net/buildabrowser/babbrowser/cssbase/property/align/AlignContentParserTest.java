package net.buildabrowser.babbrowser.cssbase.property.align;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class AlignContentParserTest {
  
  private final AlignContentParser alignContentParser = new AlignContentParser();

  @Test
  @DisplayName("Can parse align content value")
  public void canParseAlignContentValue() throws IOException {
    CSSValue value = alignContentParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("space-around")));
    Assertions.assertEquals(AlignContentValue.SPACE_AROUND, value);
  }

}
