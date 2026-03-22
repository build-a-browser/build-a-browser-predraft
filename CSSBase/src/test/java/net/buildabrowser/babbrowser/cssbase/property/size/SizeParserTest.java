package net.buildabrowser.babbrowser.cssbase.property.size;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class SizeParserTest {

  private static final SizeParser sizeParser = new SizeParser(true, false, CSSProperty.WIDTH);
  private static final SizeParser disabledSizeParser = new SizeParser(false, false, CSSProperty.WIDTH);
  private static final SizeParser minMaxParser = new SizeParser(true, false, true, true, CSSProperty.WIDTH);
  
  @Test
  @DisplayName("Can parse length size value")
  public void canParseLengthSizeValue() throws IOException {
    CSSValue value = sizeParser.parse(
      CSSTokenStream.create(DimensionToken.create(4, "em")));
    Assertions.assertEquals(
      LengthValue.create(4, true, LengthType.EM),
      value);
  }

  @Test
  @DisplayName("Can parse length size value of zero")
  public void canParseLengthSizeValueOfZero() throws IOException {
    CSSValue value = sizeParser.parse(
      CSSTokenStream.create(NumberToken.create(0)));
    Assertions.assertEquals(
      LengthValue.create(0, true, null),
      value);
  }

  @Test
  @DisplayName("Can parse percentage size value")
  public void canParsePercentageSizeValue() throws IOException {
    CSSValue value = sizeParser.parse(
      CSSTokenStream.create(PercentageToken.create(4)));
    Assertions.assertEquals(PercentageValue.create(4), value);
  }

  @Test
  @DisplayName("Can parse none size value when enabled")
  public void canParseNoneSizeValueWhenEnabled() throws IOException {
    CSSValue value = sizeParser.parse(
      CSSTokenStream.create(IdentToken.create("none")));
    Assertions.assertEquals(CSSValue.NONE, value);
  }

  @Test
  @DisplayName("Cannot parse none size value when disabled")
  public void cannotParseNoneSizeValueWhenDisabled() throws IOException {
    CSSValue value = disabledSizeParser.parse(
      CSSTokenStream.create(IdentToken.create("none")));
    Assertions.assertTrue(value.isFailure());
  }

  @Test
  @DisplayName("Can parse min-content size value when enabled")
  public void canParseMinContentSizeValueWhenEnabled() throws IOException {
    CSSValue value = minMaxParser.parse(
      CSSTokenStream.create(IdentToken.create("min-content")));
    Assertions.assertEquals(SizeValue.MIN_CONTENT, value);
  }

  @Test
  @DisplayName("Can parse fit-content size value when enabled")
  public void canParseFitContentSizeValueWhenEnabled() throws IOException {
    CSSValue value = minMaxParser.parse(
      CSSTokenStream.create(new FunctionValue("fit-content", List.of(PercentageToken.create(50)))));
    Assertions.assertEquals(SizeValue.FitContent.create(PercentageValue.create(50)), value);
  }

}
