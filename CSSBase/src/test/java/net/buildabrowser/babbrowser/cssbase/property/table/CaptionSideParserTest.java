package net.buildabrowser.babbrowser.cssbase.property.table;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class CaptionSideParserTest {
  
  private final CaptionSideParser captionSideParser = new CaptionSideParser();
  
  @Test
  @DisplayName("Can parse caption-side value")
  public void canParseCaptionSideValue() throws IOException {
    CSSValue value = captionSideParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("bottom")));
    Assertions.assertEquals(CaptionSideValue.BOTTOM, value);
  }

}
