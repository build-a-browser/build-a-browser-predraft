package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.ListResult;
import net.buildabrowser.babbrowser.css.engine.property.box.VisualBoxValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundClipParserTest {
  
  private final BackgroundClipParser backgroundClipParser = new BackgroundClipParser();

  @Test
  @DisplayName("Can parse single clip value")
  public void canParseSingleClipValue() throws IOException {
    CSSValue value = backgroundClipParser.parse(
      CSSTokenStream.create(
        IdentToken.create("border-box")));
    Assertions.assertEquals(
      ListResult.create(
        VisualBoxValue.BORDER_BOX),
      value);
  }

  @Test
  @DisplayName("Can parse multiple clip values")
  public void canParseMultipleClipValues() throws IOException {
    CSSValue value = backgroundClipParser.parse(
      CSSTokenStream.create(
        IdentToken.create("padding-box"),
        CommaToken.create(),
        IdentToken.create("content-box")));
    Assertions.assertEquals(
      ListResult.create(
        VisualBoxValue.PADDING_BOX,
        VisualBoxValue.CONTENT_BOX),
      value);
  }

}
