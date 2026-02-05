package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;

public class HexColorParserTest {

  private final HexColorParser hexColorParser = new HexColorParser();
  
  @Test
  @DisplayName("Can parse six-component color")
  public void canParseSixComponentColor() throws IOException {
    CSSValue value = hexColorParser.parse(
      CSSTokenStream.create(HashToken.create("babbab", HashToken.Type.ID)));
    Assertions.assertEquals(SRGBAColor.create(186, 187, 171, 255), value);
  }

}
