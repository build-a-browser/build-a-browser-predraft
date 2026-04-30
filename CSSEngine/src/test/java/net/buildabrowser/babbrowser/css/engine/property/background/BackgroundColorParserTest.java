package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.ListResult;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundColorParserTest {
  
  private final BackgroundColorParser backgroundColorParser = new BackgroundColorParser();

  @Test
  @DisplayName("Can parse single background color")
  public void canParseSingleBackgroundColor() throws IOException {
    CSSValue value = backgroundColorParser.parse(
      CSSTokenStream.create(
        HashToken.create("babbab", HashToken.Type.ID)));
    Assertions.assertEquals(
      ListResult.create(
        SRGBAColor.create(186, 187, 171, 255)),
      value);
  }

  @Test
  @DisplayName("Can parse multiple background colors")
  public void canParseMultipleBackgroundColors() throws IOException {
    CSSValue value = backgroundColorParser.parse(
      CSSTokenStream.create(
        IdentToken.create("rebeccapurple"),
        CommaToken.create(),
        HashToken.create("babbab", HashToken.Type.ID)));
    Assertions.assertEquals(
      ListResult.create(
        SRGBAColor.create(102, 51, 153, 255),
        SRGBAColor.create(186, 187, 171, 255)),
      value);
  }

}
