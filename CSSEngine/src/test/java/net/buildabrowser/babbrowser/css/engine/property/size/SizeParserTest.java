package net.buildabrowser.babbrowser.css.engine.property.size;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class SizeParserTest {

  private static final SizeParser sizeParser = new SizeParser(true, false, CSSProperty.WIDTH);
  private static final SizeParser disabledSizeParser = new SizeParser(false, false, CSSProperty.WIDTH);
  
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
      CSSTokenStream.create(DimensionToken.create(0, null)));
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

}
