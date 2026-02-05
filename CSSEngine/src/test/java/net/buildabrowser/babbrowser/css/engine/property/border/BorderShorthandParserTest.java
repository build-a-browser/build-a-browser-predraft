package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BorderShorthandParserTest {
  
  private final BorderShorthandParser borderShorthandParser = new BorderShorthandParser();

  @Test
  @DisplayName("Can parse border shorthand with all components")
  public void canParseBorderShorthandWithAllComponents() throws IOException {
    CSSValue value = borderShorthandParser.parse(
      CSSTokenStream.create(
        IdentToken.create("red"),
        DimensionToken.create(1, "px"),
        IdentToken.create("solid")));
      
    Assertions.assertEquals(new BorderCompositeValue(
      LengthValue.create(1, true, LengthType.PX),
      SRGBAColor.create(255, 0, 0, 255),
      BorderStyleValue.SOLID
    ), value);
  }

  @Test
  @DisplayName("Can parse border shorthand with just two components")
  public void canParseBorderShorthandWithTwoComponents() throws IOException {
    CSSValue value = borderShorthandParser.parse(
      CSSTokenStream.create(
        IdentToken.create("red"),
        IdentToken.create("solid")));

    Assertions.assertEquals(new BorderCompositeValue(
      null,
      SRGBAColor.create(255, 0, 0, 255),
      BorderStyleValue.SOLID
    ), value);
  }

}
