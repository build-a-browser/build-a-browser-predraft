package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;

public class BackgroundColorParserTest {
  
  private final BackgroundColorParser backgroundColorParser = new BackgroundColorParser();

  @Test
  @DisplayName("Can parse single background color")
  public void canParseSingleBackgroundColor() throws IOException {
    CSSValue value = backgroundColorParser.parse(
      CSSTokenStream.createForTesting(
        HashToken.create("babbab", HashToken.Type.ID)));
    Assertions.assertEquals(
      SRGBAColor.create(186, 187, 171, 255),
      value);
  }

}
