package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BorderStyleParserTest {
  
  private final BorderStyleParser borderStyleParser = new BorderStyleParser(CSSProperty.BORDER_RIGHT_STYLE);

  @Test
  @DisplayName("Can parse named border style")
  public void canParseNamedBorderStyle() throws IOException {
    CSSValue value = borderStyleParser.parse(
      CSSTokenStream.create(IdentToken.create("solid")));
    Assertions.assertEquals(BorderStyleValue.SOLID, value);
  }

}
