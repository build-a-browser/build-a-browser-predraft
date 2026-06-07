package net.buildabrowser.babbrowser.cssbase.property.content;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.content.ContentValue.StringContentValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class ContentParserTest {

  private final ContentParser contentParser = new ContentParser();
  
  @Test
  @DisplayName("Can parse normal content value")
  public void canParseNormalContentValue() throws IOException {
    CSSValue value = contentParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("normal")));
    Assertions.assertEquals(
      ContentValue.NORMAL,
      value);
  }

  @Test
  @DisplayName("Can parse string content value")
  public void canParseStringValue() throws IOException {
    CSSValue value = contentParser.parse(
      CSSTokenStream.createForTesting(StringToken.create("test")));
    Assertions.assertEquals(
      StringContentValue.create("test"),
      value);
  }

}
