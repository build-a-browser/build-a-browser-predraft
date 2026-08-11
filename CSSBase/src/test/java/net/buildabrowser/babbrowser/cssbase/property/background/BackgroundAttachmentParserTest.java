package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundAttachmentParserTest {

  private final BackgroundAttachmentParser backgroundAttachmentParser = new BackgroundAttachmentParser();

  @Test
  @DisplayName("Can parse single attachment value")
  public void canParseSingleAttachmentValue() throws IOException {
    CSSValue value = backgroundAttachmentParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("fixed")));
    Assertions.assertEquals(
      ManyResult.createCommas(
        BackgroundAttachmentValue.FIXED),
      value);
  }

  @Test
  @DisplayName("Can parse multiple attachment value")
  public void canParseMultipleAttachmentValue() throws IOException {
    CSSValue value = backgroundAttachmentParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("scroll"),
        CommaToken.create(),
        IdentToken.create("local")));
    Assertions.assertEquals(
      ManyResult.createCommas(
        BackgroundAttachmentValue.SCROLL,
        BackgroundAttachmentValue.LOCAL),
      value);
  }
  
}
