package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class BackgroundSizeParserTest {
  
  private final BackgroundSizeParser backgroundSizeParser = new BackgroundSizeParser();

  @Test
  @DisplayName("Can parse single-component named size value")
  public void canParseSingleComponentNamedSizeValue() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("cover")));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundSizeValue.COVER),
      value);
  }

  @Test
  @DisplayName("Can parse single-component percentage size value")
  public void canParseSingleComponentPercentageSizeValue() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.createForTesting(
        PercentageToken.create(5)));
    Assertions.assertEquals(
      ManyResult.create(
        SizedBackgroundSizeValue.create(
          PercentageValue.create(5), CSSValue.AUTO)),
      value);
  }

  @Test
  @DisplayName("Can parse double-component percentage size value")
  public void canParseDoubleComponentPercentageSizeValue() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto"),
        PercentageToken.create(7)));
    Assertions.assertEquals(
      ManyResult.create(
        SizedBackgroundSizeValue.create(
          CSSValue.AUTO, PercentageValue.create(7))),
      value);
  }

  @Test
  @DisplayName("Can parse size value with length unit")
  public void canParseSizeValueWithLengthUnit() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto"),
        DimensionToken.create(5, true, "em")));
    Assertions.assertEquals(
      ManyResult.create(
        SizedBackgroundSizeValue.create(
          CSSValue.AUTO, LengthValue.create(5, true, LengthType.EM))),
      value);
  }

  @Test
  @DisplayName("Can parse multiple size values")
  public void canParseMultipleSizeValues() throws IOException {
    CSSValue value = backgroundSizeParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto"),
        PercentageToken.create(7),
        CommaToken.create(),
        IdentToken.create("contain")));
    Assertions.assertEquals(
      ManyResult.create(
        SizedBackgroundSizeValue.create(
          CSSValue.AUTO, PercentageValue.create(7)),
        BackgroundSizeValue.CONTAIN),
      value);
  }

}
