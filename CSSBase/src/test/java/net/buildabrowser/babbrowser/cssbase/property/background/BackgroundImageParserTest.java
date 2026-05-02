package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.shared.URLValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.URLToken;

public class BackgroundImageParserTest {

  private static final URI TESTING_URL = CommonUtil.rethrow(() -> new URI("about:blank"));
  
  private final BackgroundImageParser backgroundImageParser = new BackgroundImageParser();

  @Test
  @DisplayName("Can parse single background image")
  public void canParseSingleBackgroundImage() throws IOException {
    CSSValue value = backgroundImageParser.parse(
      CSSTokenStream.createForTesting(
        URLToken.create("kumo.css")));
    Assertions.assertEquals(
      ManyResult.create(
        URLValue.create("kumo.css", TESTING_URL)),
      value);
  }

  @Test
  @DisplayName("Can parse multiple background images")
  public void canParseMultipleBackgroundImages() throws IOException {
    CSSValue value = backgroundImageParser.parse(
      CSSTokenStream.createForTesting(
        new FunctionValue("url", List.of(StringToken.create("spider.css"))),
        CommaToken.create(),
        IdentToken.create("none"),
        CommaToken.create(),
        new FunctionValue("src", List.of(StringToken.create("web.css")))));
    Assertions.assertEquals(
      ManyResult.create(
        URLValue.create("spider.css", TESTING_URL),
        CSSValue.NONE,
        URLValue.create("web.css", TESTING_URL)),
      value);
  }

}
