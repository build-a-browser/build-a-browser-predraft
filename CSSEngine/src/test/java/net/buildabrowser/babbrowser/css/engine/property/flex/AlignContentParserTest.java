package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class AlignContentParserTest {
  
  private final AlignContentParser alignContentParser = new AlignContentParser();

  @Test
  @DisplayName("Can parse align content value")
  public void canParseAlignContentValue() throws IOException {
    CSSValue value = alignContentParser.parse(
      CSSTokenStream.create(IdentToken.create("space-around")));
    Assertions.assertEquals(AlignContentValue.SPACE_AROUND, value);
  }

}
