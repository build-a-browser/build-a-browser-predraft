package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue.BackgroundPositionSide;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class BackgroundPositionParserTest {

  private static final PercentageValue ZERO_PERCENT = PercentageValue.create(0);
  
  private final BackgroundPositionParser backgroundPositionParser = new BackgroundPositionParser();

  @Test
  @DisplayName("Can parse single-component horizontal named background position")
  public void canParseSingleComponentHorizontalNamedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("right")));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.RIGHT, ZERO_PERCENT,
          BackgroundPositionSide.CENTER, ZERO_PERCENT)),
      value);
  }

  @Test
  @DisplayName("Can parse single-component vertical named background position")
  public void canParseSingleComponentVerticalNamedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("bottom")));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.CENTER, ZERO_PERCENT,
          BackgroundPositionSide.BOTTOM, ZERO_PERCENT)),
      value);
  }
  
  @Test
  @DisplayName("Can parse single-component horizontal percentage background position")
  public void canParseSingleComponentHorizontaPercentageBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        PercentageToken.create(5)));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.LEFT, PercentageValue.create(5),
          BackgroundPositionSide.CENTER, ZERO_PERCENT)),
      value);
  }

  @Test
  @DisplayName("Can parse double-component named background position")
  public void canParseDoubleComponentNamedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("right"),
        IdentToken.create("bottom")));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.RIGHT, ZERO_PERCENT,
          BackgroundPositionSide.BOTTOM, ZERO_PERCENT)),
      value);
  }

  @Test
  @DisplayName("Can parse reversed double-component named background position")
  public void canParseReversedDoubleComponentNamedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("bottom"),
        IdentToken.create("center")));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.CENTER, ZERO_PERCENT,
          BackgroundPositionSide.BOTTOM, ZERO_PERCENT)),
      value);
  }

  @Test
  @DisplayName("Can parse double-component percentage background position")
  public void canParseDoubleComponentPercentageBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        PercentageToken.create(6),
        PercentageToken.create(9)));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.LEFT, PercentageValue.create(6),
          BackgroundPositionSide.TOP, PercentageValue.create(9))),
      value);
  }


  @Test
  @DisplayName("Can parse double-component mixed background position")
  public void canParseDoubleComponentMixedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("left"),
        PercentageToken.create(9)));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.LEFT, ZERO_PERCENT,
          BackgroundPositionSide.TOP, PercentageValue.create(9))),
      value);
  }

  @Test
  @DisplayName("Can parse triple-component mixed background position")
  public void canParseTripleComponentMixedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("center"),
        IdentToken.create("left"),
        PercentageToken.create(9)));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.LEFT, PercentageValue.create(9),
          BackgroundPositionSide.CENTER, ZERO_PERCENT)),
      value);
  }



  @Test
  @DisplayName("Can parse quadruple-component mixed background position")
  public void canParseQuadrupleComponentMixedBackgroundPosition() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("right"),
        PercentageToken.create(7),
        IdentToken.create("bottom"),
        PercentageToken.create(8)));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.RIGHT, PercentageValue.create(7),
          BackgroundPositionSide.BOTTOM, PercentageValue.create(8))),
      value);
  }

  @Test
  @DisplayName("Can parse multiple background positions")
  public void canParseMultipleBackgroundPositions() throws IOException {
    CSSValue value = backgroundPositionParser.parse(
      CSSTokenStream.create(
        IdentToken.create("bottom"),
        CommaToken.create(),
        IdentToken.create("center"),
        IdentToken.create("bottom")));
    Assertions.assertEquals(
      ManyResult.create(
        BackgroundPositionValue.create(
          BackgroundPositionSide.CENTER, ZERO_PERCENT,
          BackgroundPositionSide.BOTTOM, ZERO_PERCENT),
        BackgroundPositionValue.create(
          BackgroundPositionSide.CENTER, ZERO_PERCENT,
          BackgroundPositionSide.BOTTOM, ZERO_PERCENT)),
      value);
  }

}
