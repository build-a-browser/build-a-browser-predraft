package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.ListResult;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.css.engine.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class BackgroundSizeParserTest {
  
  private final BackgroundSizeParser backgroundSizeParser = new BackgroundSizeParser();

  @Test
  @DisplayName("Can parse single-component named size value")
  public void canParseSingleComponentNamedSizeValue() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.create(
        IdentToken.create("cover")));
    Assertions.assertEquals(
      ListResult.create(
        BackgroundSizeValue.COVER),
      value);
  }

  @Test
  @DisplayName("Can parse single-component percentage size value")
  public void canParseSingleComponentPercentageSizeValue() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.create(
        PercentageToken.create(5)));
    Assertions.assertEquals(
      ListResult.create(
        SizedBackgroundSizeValue.create(
          PercentageValue.create(5), CSSValue.AUTO)),
      value);
  }

  @Test
  @DisplayName("Can parse double-component percentage size value")
  public void canParseDoubleComponentPercentageSizeValue() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.create(
        IdentToken.create("auto"),
        PercentageToken.create(7)));
    Assertions.assertEquals(
      ListResult.create(
        SizedBackgroundSizeValue.create(
          CSSValue.AUTO, PercentageValue.create(7))),
      value);
  }

  @Test
  @DisplayName("Can parse multiple size values")
  public void canParseMultipleSizeValues() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.create(
        IdentToken.create("auto"),
        PercentageToken.create(7),
        CommaToken.create(),
        IdentToken.create("contain")));
    Assertions.assertEquals(
      ListResult.create(
        SizedBackgroundSizeValue.create(
          CSSValue.AUTO, PercentageValue.create(7)),
        BackgroundSizeValue.CONTAIN),
      value);
  }

}
