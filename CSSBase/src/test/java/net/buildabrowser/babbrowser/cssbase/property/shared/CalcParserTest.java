package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcClampFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncDouble;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncMany;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncSingle;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcKeyword;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcLogFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcNumber;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcRoundFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcType;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.RoundingStrategy;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class CalcParserTest {

  private final CalcParser calcParser = new CalcParser(null, this::fallbackParse);

  @Test
  @DisplayName("Can parse fallback value")
  public void canParseFallbackValue() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      PercentageToken.create(5)));
    Assertions.assertEquals(PercentageValue.create(5), value);
  }

  @Test
  @DisplayName("Can parse calc with single value")
  public void canParseCalcWithSingleValue() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5)))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      PercentageValue.create(5)), value);
  }

  @Test
  @DisplayName("Can parse calc with addition")
  public void canParseCalcWithAddition() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('+'),
        PercentageToken.create(5)))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.ADD, PercentageValue.create(5), PercentageValue.create(5))
    ), value);
  }

  @Test
  @DisplayName("Can parse calc with addition and subtraction")
  public void canParseCalcWithAdditionAndSubtraction() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('+'),
        PercentageToken.create(4),
        DelimToken.create('-'),
        PercentageToken.create(3)))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.SUB,
        new CalcFuncDouble(CalcType.ADD, PercentageValue.create(5), PercentageValue.create(4)),
        PercentageValue.create(3))
    ), value);
  }

  @Test
  @DisplayName("Can parse calc with multiplication")
  public void canParseCalcWithMultiplication() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('*'),
        NumberToken.create(4)))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.MUL, PercentageValue.create(5), new CalcNumber(4, true))
    ), value);
  }

  @Test
  @DisplayName("Can parse calc with addition and division")
  public void canParseCalcWithAdditionAndDivision() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('+'),
        PercentageToken.create(4),
        DelimToken.create('/'),
        NumberToken.create(3)))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.ADD,
        PercentageValue.create(5),
        new CalcFuncDouble(CalcType.DIV, PercentageValue.create(4), new CalcNumber(3, true)))
    ), value);
  }

  @Test
  @DisplayName("Can parse calc with parentheses")
  public void canParseCalcWithParentheses() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('-'),
        new SimpleBlock(LParenToken.create(), List.of(
          PercentageToken.create(1),
          DelimToken.create('+'),
          PercentageToken.create(2)))))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.SUB,
        PercentageValue.create(5),
        new CalcFuncDouble(CalcType.ADD,
          PercentageValue.create(1),
          PercentageValue.create(2)))
    ), value);
  }

  @Test
  @DisplayName("Can parse calc with keyword")
  public void canParseCalcWithKeyword() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        IdentToken.create("e")))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      CalcKeyword.E), value);
  }

  @Test
  @DisplayName("Can parse non-trivial calc expression")
  public void canParseNonTrivialCalcExpression() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('+'),
        IdentToken.create("e"),
        DelimToken.create('*'),
        new SimpleBlock(LParenToken.create(), List.of(
          PercentageToken.create(2),
          DelimToken.create('+'),
          PercentageToken.create(3)))))));
    
    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.ADD,
        PercentageValue.create(5),
        new CalcFuncDouble(CalcType.MUL,
          CalcKeyword.E,
          new CalcFuncDouble(CalcType.ADD,
            PercentageValue.create(2),
            PercentageValue.create(3))))
    ), value);
  }

  @Test
  @DisplayName("Can parse min function")
  public void canParseMinFunction() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("min", List.of(
        PercentageToken.create(5),
        CommaToken.create(),
        PercentageToken.create(4),
        CommaToken.create(),
        PercentageToken.create(3)))));

    Assertions.assertEquals(new CalcFuncMany(CalcType.MIN,List.of(
      PercentageValue.create(5),
      PercentageValue.create(4),
      PercentageValue.create(3)
    )), value);
  }

  @Test
  @DisplayName("Can parse calc function with nested min function")
  public void canParseCalcFunctionWithNestedMinFunction() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("calc", List.of(
        PercentageToken.create(5),
        DelimToken.create('+'),
        new FunctionValue("min", List.of(
          PercentageToken.create(4),
          CommaToken.create(),
          PercentageToken.create(3)))))));

    Assertions.assertEquals(new CalcFuncSingle(CalcType.CALC,
      new CalcFuncDouble(CalcType.ADD,
        PercentageValue.create(5),
        new CalcFuncMany(CalcType.MIN, List.of(
          PercentageValue.create(4),
          PercentageValue.create(3)
        )))
    ), value);
  }

  @Test
  @DisplayName("Can parse clamp function with none")
  public void canParseClampFunctionWithNone() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("clamp", List.of(
        IdentToken.create("none"),
        CommaToken.create(),
        PercentageToken.create(5),
        CommaToken.create(),
        IdentToken.create("none")))));
    
    Assertions.assertEquals(new CalcClampFunc(CalcType.CLAMP,
      CSSValue.NONE,
      PercentageValue.create(5),
      CSSValue.NONE
    ), value);
  }

  @Test
  @DisplayName("Can parse clamp function with specified values")
  public void canParseClampFunctionWithSpecifiedValues() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("clamp", List.of(
        PercentageToken.create(1),
        CommaToken.create(),
        PercentageToken.create(5),
        CommaToken.create(),
        PercentageToken.create(10)))));
    
    Assertions.assertEquals(new CalcClampFunc(CalcType.CLAMP,
      PercentageValue.create(1),
      PercentageValue.create(5),
      PercentageValue.create(10)
    ), value);
  }

  @Test
  @DisplayName("Can parse round function with all arguments")
  public void canParseRoundFunctionWithAllArguments() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("round", List.of(
        IdentToken.create("to-zero"),
        CommaToken.create(),
        PercentageToken.create(5),
        CommaToken.create(),
        PercentageToken.create(6)))));

    Assertions.assertEquals(new CalcRoundFunc(CalcType.ROUND,
      RoundingStrategy.TO_ZERO,
      PercentageValue.create(5),
      PercentageValue.create(6)
    ), value);
  }

  @Test
  @DisplayName("Can parse round function with just one argument")
  public void canParseRoundFunctionWithJustOneArgument() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("round", List.of(
        PercentageToken.create(5)))));

    Assertions.assertEquals(new CalcRoundFunc(CalcType.ROUND,
      RoundingStrategy.NEAREST,
      PercentageValue.create(5),
      null
    ), value);
  }

  @Test
  @DisplayName("Can parse rem function")
  public void canParseRemFunction() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("rem", List.of(
        NumberToken.create(5),
        CommaToken.create(),
        NumberToken.create(3)))));
      
    Assertions.assertEquals(new CalcFuncDouble(CalcType.REM,
      new CalcNumber(5, true),
      new CalcNumber(3, true)
    ), value);
  }

  @Test
  @DisplayName("Can parse log function with one argument")
  public void canParseLogFunctionWithOneArgument() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("log", List.of(
        NumberToken.create(5)))));
      
    Assertions.assertEquals(new CalcLogFunc(CalcType.LOG,
      new CalcNumber(5, true),
      null
    ), value);
  }

  @Test
  @DisplayName("Can parse log function with two arguments")
  public void canParseLogFunctionWithTwoArguments() throws IOException {
    CSSValue value = calcParser.parse(CSSTokenStream.createForTesting(
      new FunctionValue("log", List.of(
        NumberToken.create(5),
        CommaToken.create(),
        NumberToken.create(3)))));

    Assertions.assertEquals(new CalcLogFunc(CalcType.LOG,
      new CalcNumber(5, true),
      new CalcNumber(3, true)
    ), value);
  }

  private CSSValue fallbackParse(CSSTokenStream stream) throws IOException {
    if (stream.read() instanceof PercentageToken percentageToken) {
      return PercentageValue.create(percentageToken.value());
    } else {
      return new CSSFailure("Expected a percentage token!");
    }
  }

}
