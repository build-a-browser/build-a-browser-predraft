package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.ListResult;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundRepeatValue.BackgroundAxisRepeatValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundRepeatParserTest {
  
  private final BackgroundRepeatParser backgroundRepeatParser = new BackgroundRepeatParser();

  @Test
  @DisplayName("Can parse single repeat value")
  public void canParseSingleRepeatValue() throws IOException {
    CSSValue value = backgroundRepeatParser.parse(
      CSSTokenStream.create(
        IdentToken.create("repeat-y")));
    Assertions.assertEquals(
      ListResult.create(
        BackgroundRepeatValue.create(
          BackgroundAxisRepeatValue.NO_REPEAT,
          BackgroundAxisRepeatValue.REPEAT)),
      value);
  }

  @Test
  @DisplayName("Can parse multiple repeat values")
  public void canParseMultipleRepeatValues() throws IOException {
    CSSValue value = backgroundRepeatParser.parse(
      CSSTokenStream.create(
        IdentToken.create("space"),
        IdentToken.create("round"),
        CommaToken.create(),
        IdentToken.create("repeat-x"),
        CommaToken.create(),
        IdentToken.create("repeat-y")));
    Assertions.assertEquals(
      ListResult.create(
        BackgroundRepeatValue.create(
          BackgroundAxisRepeatValue.SPACE,
          BackgroundAxisRepeatValue.ROUND),
        BackgroundRepeatValue.create(
          BackgroundAxisRepeatValue.REPEAT,
          BackgroundAxisRepeatValue.NO_REPEAT),
        BackgroundRepeatValue.create(
          BackgroundAxisRepeatValue.NO_REPEAT,
          BackgroundAxisRepeatValue.REPEAT)),
      value);
  }

}
