package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.ListResult;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundAttachmentParserTest {

  private final BackgroundAttachmentParser backgroundAttachmentParser = new BackgroundAttachmentParser();

  @Test
  @DisplayName("Can parse single attachment value")
  public void canParseSingleAttachmentValue() throws IOException {
    CSSValue value = backgroundAttachmentParser.parse(
      CSSTokenStream.create(
        IdentToken.create("fixed")));
    Assertions.assertEquals(
      ListResult.create(
        BackgroundAttachmentValue.FIXED),
      value);
  }

  @Test
  @DisplayName("Can parse multiple attachment value")
  public void canParseMultipleAttachmentValue() throws IOException {
    CSSValue value = backgroundAttachmentParser.parse(
      CSSTokenStream.create(
        IdentToken.create("scroll"),
        CommaToken.create(),
        IdentToken.create("local")));
    Assertions.assertEquals(
      ListResult.create(
        BackgroundAttachmentValue.SCROLL,
        BackgroundAttachmentValue.LOCAL),
      value);
  }
  
}
