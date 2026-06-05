package net.buildabrowser.babbrowser.cssbase.property.color;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class NamedColorParserTest {

  private final NamedColorParser namedColorParser = new NamedColorParser();

  @BeforeAll
  public static void beforeAll() {
    ColorParserTestUtil.initColors();
  }
  
  @Test
  @DisplayName("Can parse named color")
  public void canParseNamedColor() throws IOException {
    CSSValue value = namedColorParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("rebeccapurple")));
    Assertions.assertEquals(SRGBAColor.create(102, 51, 153, 255), value);
  }

}
