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
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class OutlineColorParserTest {
  
  private final OutlineColorParser outlineColorParser = new OutlineColorParser();
  
  @BeforeAll
  public static void beforeAll() {
    ColorParserTestUtil.initColors();
  }

  @Test
  @DisplayName("Can parse outline with named color")
  public void canParseOutlineWithNamedColor() throws IOException {
    CSSValue value = outlineColorParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("red")));
    Assertions.assertEquals(SRGBAColor.create(255, 0, 0, 255), value);
  }

  @Test
  @DisplayName("Can parse outline with auto color")
  public void canParseOutlineWithAutoColor() throws IOException {
    CSSValue value = outlineColorParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("auto")));
    Assertions.assertEquals(CSSValue.AUTO, value);
  }

}
