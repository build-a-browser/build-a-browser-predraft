package net.buildabrowser.babbrowser.cssbase.property.color;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;

public class HexColorParserTest {

  private final HexColorParser hexColorParser = new HexColorParser();
  
  @Test
  @DisplayName("Can parse six-component color")
  public void canParseSixComponentColor() throws IOException {
    CSSValue value = hexColorParser.parse(
      CSSTokenStream.createForTesting(HashToken.create("babbab", HashToken.Type.ID)));
    Assertions.assertEquals(SRGBAColor.create(186, 187, 171, 255), value);
  }

}
