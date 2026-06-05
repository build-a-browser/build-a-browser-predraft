package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue.BackgroundPositionSide;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundValue.BackgroundLayer;
import net.buildabrowser.babbrowser.cssbase.property.box.VisualBoxValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorParserTestUtil;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.property.shared.URLValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.URLToken;

public class BackgroundParserTest {

  private static final URI TESTING_URL = CommonUtil.rethrow(() -> new URI("about:blank"));

  private final BackgroundParser backgroundParser = new BackgroundParser();

  @BeforeAll
  public static void beforeAll() {
    ColorParserTestUtil.initColors();
  }

  @Test
  @DisplayName("Can parse background shorthand with just URL")
  public void canParseBackgroundShorthandWithJustURL() throws IOException {
    CSSValue value = backgroundParser.parse(
      CSSTokenStream.createForTesting(
        URLToken.create("background.css")));
    Assertions.assertEquals(
      BackgroundValue.create(List.of(
        BackgroundLayer.create(
          URLValue.create("background.css", TESTING_URL),
          null, null, null, null,
          null, null, null))),
      value);
  }

  @Test
  @DisplayName("Can parse background shorthand with color in last layer")
  public void canParseBackgroundShorthandWithColorInLastLayer() throws IOException {
    CSSValue value = backgroundParser.parse(
      CSSTokenStream.createForTesting(
        URLToken.create("background.css"),
        CommaToken.create(),
        IdentToken.create("rebeccapurple")));
    Assertions.assertEquals(
      BackgroundValue.create(List.of(
        BackgroundLayer.create(
          URLValue.create("background.css", TESTING_URL),
          null, null, null, null,
          null, null, null),
        BackgroundLayer.create(
          null, null, null, null, null, null, null,
          SRGBAColor.create(102, 51, 153, 255)))),
      value);
  }

  @Test
  @DisplayName("Can parse background shorthand with position")
  public void canParseBackgroundShorthandWithPosition() throws IOException {
    CSSValue value = backgroundParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("center"),
        IdentToken.create("left"),
        PercentageToken.create(9)));
    Assertions.assertEquals(
      BackgroundValue.create(List.of(
        BackgroundLayer.create(
          null,
          BackgroundPositionValue.create(
            BackgroundPositionSide.LEFT, PercentageValue.create(9),
            BackgroundPositionSide.CENTER, PercentageValue.create(0)),
          null, null, null,
          null, null, null))),
      value);
  }

  @Test
  @DisplayName("Can parse background shorthand with position and size")
  public void canParseBackgroundShorthandWithPositionAndSize() throws IOException {
    CSSValue value = backgroundParser.parse(
      CSSTokenStream.createForTesting(
        PercentageToken.create(1),
        PercentageToken.create(2),
        DelimToken.create('/'),
        PercentageToken.create(3),
        PercentageToken.create(4)));
    Assertions.assertEquals(
      BackgroundValue.create(List.of(
        BackgroundLayer.create(
          null,
          BackgroundPositionValue.create(
            BackgroundPositionSide.LEFT, PercentageValue.create(1),
            BackgroundPositionSide.TOP, PercentageValue.create(2)),
          SizedBackgroundSizeValue.create(
            PercentageValue.create(3),
            PercentageValue.create(4)),
          null, null,
          null, null, null))),
      value);
  }

  @Test
  @DisplayName("Can parse background shorthand with same origin and clip")
  public void canParseBackgroundShorthandWithSameOriginAndClip() throws IOException {
    CSSValue value = backgroundParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("content-box")));
    Assertions.assertEquals(
      BackgroundValue.create(List.of(
        BackgroundLayer.create(
          null, null, null, null, null,
          VisualBoxValue.CONTENT_BOX, VisualBoxValue.CONTENT_BOX, null))),
      value);
  }

  @Test
  @DisplayName("Can parse background shorthand with differing origin and clip")
  public void canParseBackgroundShorthandWithDifferingOriginAndClip() throws IOException {
    CSSValue value = backgroundParser.parse(
      CSSTokenStream.createForTesting(
        IdentToken.create("padding-box"),
        IdentToken.create("border-box")));
    Assertions.assertEquals(
      BackgroundValue.create(List.of(
        BackgroundLayer.create(
          null, null, null, null, null,
          VisualBoxValue.PADDING_BOX, VisualBoxValue.BORDER_BOX, null))),
      value);
  }

}
