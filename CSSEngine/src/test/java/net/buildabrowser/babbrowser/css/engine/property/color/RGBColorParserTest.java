package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class RGBColorParserTest {
  
  private final RGBColorParser rgbColorParser = new RGBColorParser();

  @Test
  @DisplayName("Can parse legacy with numbers - rgb(1,0,0,.5)")
  public void canParseLegacyWithNumbers() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(1),
      CommaToken.create(),
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(.5, false)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(1, colorValue.red());
    Assertions.assertEquals(0, colorValue.green());
    Assertions.assertEquals(0, colorValue.blue());
    Assertions.assertEquals(127, colorValue.alpha());
  }

  @Test
  @DisplayName("Can parse legacy with percents - rgb(5%,0%,1%,50%)")
  public void canParseLegacyWithPercents() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      PercentageToken.create(5),
      CommaToken.create(),
      PercentageToken.create(0),
      CommaToken.create(),
      PercentageToken.create(1),
      CommaToken.create(),
      PercentageToken.create(50)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(12, colorValue.red());
    Assertions.assertEquals(0, colorValue.green());
    Assertions.assertEquals(2, colorValue.blue());
    Assertions.assertEquals(127, colorValue.alpha());
  }

  @Test
  @DisplayName("Can parse legacy with whitespace - rgb(0, 0, 0)")
  public void canParseLegacyWithWhitespace() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      CommaToken.create(),
      WhitespaceToken.create(),
      NumberToken.create(0),
      CommaToken.create(),
      WhitespaceToken.create(),
      NumberToken.create(0)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(0, colorValue.red());
    Assertions.assertEquals(0, colorValue.green());
    Assertions.assertEquals(0, colorValue.blue());
    Assertions.assertEquals(255, colorValue.alpha());
  }

  @Test
  @DisplayName("Cannot mix percentage and number in legacy - rgb(0,0%,0,0)")
  public void cannotMixPercentageAndNumberInLegacy() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      CommaToken.create(),
      PercentageToken.create(0),
      CommaToken.create(),
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertTrue(result.isFailure());
  }

  @Test
  @DisplayName("Can parse modern with percents - rgb(1%1%1%)")
  public void canParseRGB1p1p1p() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      PercentageToken.create(1),
      PercentageToken.create(1),
      PercentageToken.create(1)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(2, colorValue.red());
    Assertions.assertEquals(2, colorValue.green());
    Assertions.assertEquals(2, colorValue.blue());
    Assertions.assertEquals(255, colorValue.alpha());
  }

  @Test
  @DisplayName("Can parse modern with numbers spaces and percent alpha - rgb(0 0 0 1%)")
  public void canParseModernWithNumbersSpacesAndPercentAlpha() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      WhitespaceToken.create(),
      NumberToken.create(0),
      WhitespaceToken.create(),
      NumberToken.create(0),
      WhitespaceToken.create(),
      PercentageToken.create(1)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(0, colorValue.red());
    Assertions.assertEquals(0, colorValue.green());
    Assertions.assertEquals(0, colorValue.blue());
    Assertions.assertEquals(2, colorValue.alpha());
  }

  @Test
  @DisplayName("Can parse modern with numbers spaces and decimal alpha - rgba(255 255 255 / .5)")
  public void canParseModernWithNumbersSpacesAndDecimalAlpha() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgba", List.of(
      NumberToken.create(255),
      WhitespaceToken.create(),
      NumberToken.create(255),
      WhitespaceToken.create(),
      NumberToken.create(255),
      WhitespaceToken.create(),
      DelimToken.create('/'),
      WhitespaceToken.create(),
      NumberToken.create(.5f, false)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(255, colorValue.red());
    Assertions.assertEquals(255, colorValue.green());
    Assertions.assertEquals(255, colorValue.blue());
    Assertions.assertEquals(127, colorValue.alpha());
  }

  @Test
  @DisplayName("Can parse modern with numbers none - rgba(3 none none / none)")
  public void canParseModernWithNone() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgba", List.of(
      NumberToken.create(3),
      WhitespaceToken.create(),
      IdentToken.create("none"),
      WhitespaceToken.create(),
      IdentToken.create("none"),
      WhitespaceToken.create(),
      DelimToken.create('/'),
      WhitespaceToken.create(),
      IdentToken.create("none")
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertFalse(result.isFailure());

    ColorValue colorValue = Assertions.assertInstanceOf(ColorValue.class, result);
    Assertions.assertEquals(3, colorValue.red());
    Assertions.assertEquals(0, colorValue.green());
    Assertions.assertEquals(0, colorValue.blue());
    Assertions.assertEquals(255, colorValue.alpha());
  }

  @Test
  @DisplayName("Cannot parse wrong number of arguments in legacy - rgb(0,0,)")
  public void cannotParseWrongNumberOfArgumentsInLegacy() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0),
      CommaToken.create()
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertTrue(result.isFailure());
  }

  @Test
  @DisplayName("Cannot parse wrong number of arguments in modern - rgb(0 0 )")
  public void cannotParseWrongNumberOfArgumentsInModern() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      WhitespaceToken.create(),
      NumberToken.create(0),
      WhitespaceToken.create()
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertTrue(result.isFailure());
  }

  @Test
  @DisplayName("Can not mix legacy and modern syntax")
  public void canNotMixLegacyAndModernSyntax() throws IOException {
    FunctionValue functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0),
      NumberToken.create(0)
    ));

    CSSValue result1 = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertTrue(result1.isFailure());

    functionValue = new FunctionValue("rgb", List.of(
      NumberToken.create(0),
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0)
    ));

    CSSValue result2 = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertTrue(result2.isFailure());
  }

  @Test
  @DisplayName("Cannot parse wrong function name")
  public void cannotParseWrongFunctionName() throws IOException {
    FunctionValue functionValue = new FunctionValue("wrong", List.of(
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0),
      CommaToken.create(),
      NumberToken.create(0)
    ));

    CSSValue result = rgbColorParser.parse(ListCSSTokenStream.create(functionValue));
    Assertions.assertTrue(result.isFailure());
  }

}
