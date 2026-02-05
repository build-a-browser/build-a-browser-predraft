package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class NamedColorParserTest {

  private final NamedColorParser namedColorParser = new NamedColorParser();
  
  @Test
  @DisplayName("Can parse named color")
  public void canParseNamedColor() throws IOException {
    CSSValue value = namedColorParser.parse(
      CSSTokenStream.create(IdentToken.create("rebeccapurple")));
    Assertions.assertEquals(SRGBAColor.create(102, 51, 153, 255), value);
  }

}
