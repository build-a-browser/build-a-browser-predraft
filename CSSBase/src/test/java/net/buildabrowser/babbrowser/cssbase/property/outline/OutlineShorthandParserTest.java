package net.buildabrowser.babbrowser.cssbase.property.outline;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorParserTestUtil;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class OutlineShorthandParserTest {
  
  private final OutlineShorthandParser outlineShorthandParser = new OutlineShorthandParser();

  @BeforeAll
  public static void beforeAll() {
    ColorParserTestUtil.initColors();
  }

  @Test
  @DisplayName("Can parse outline shorthand with all components")
  public void canParseOutlineShorthandWithAllComponents() throws IOException {
    CSSValue value = outlineShorthandParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("red"),
        DimensionToken.create(1, "px"),
        IdentToken.create("solid")));
      
    Assertions.assertEquals(new OutlineCompositeValue(
      LengthValue.create(1, true, LengthType.PX),
      LineStyleValue.SOLID,
      SRGBAColor.create(255, 0, 0, 255)
    ), value);
  }

  @Test
  @DisplayName("Can parse outline shorthand with just two components")
  public void canParseOutlineShorthandWithTwoComponents() throws IOException {
    CSSValue value = outlineShorthandParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("red"),
        IdentToken.create("solid")));

    Assertions.assertEquals(new OutlineCompositeValue(
      null,
      LineStyleValue.SOLID,
      SRGBAColor.create(255, 0, 0, 255)
    ), value);
  }

  @Test
  @DisplayName("Can parse outline shorthand with width and auto")
  public void canParseOutlineShorthandWithWidthAndAuto() throws IOException {
    CSSValue value = outlineShorthandParser.parse(
      CSSTokenStream.createForTesting(
        DimensionToken.create(1, "px"),
        IdentToken.create("auto")));

    Assertions.assertEquals(new OutlineCompositeValue(
      LengthValue.create(1, true, LengthType.PX),
      CSSValue.AUTO,
      CSSValue.AUTO
    ), value);
  }

  @Test
  @DisplayName("Can parse outline shorthand with just auto")
  public void canParseOutlineShorthandWithJustAuto() throws IOException {
    CSSValue value = outlineShorthandParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("auto")));

    Assertions.assertEquals(new OutlineCompositeValue(
      null,
      CSSValue.AUTO,
      CSSValue.AUTO
    ), value);
  }

}
