package net.buildabrowser.babbrowser.cssbase.calc;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.calc.test.CalcLengthPercentageEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcEvaluation.CalcNumberEvaluation;
import net.buildabrowser.babbrowser.cssbase.property.calc.CalcInterpreter;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcClampFunc;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncDouble;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncMany;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcFuncSingle;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcNumber;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcValue.CalcType;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;

public class CalcInterpreterTest {
 
  @Test
  @DisplayName("Can evaluate simple calc expression")
  public void canEvaluateSimpleCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcNumber(5, true));
    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcNumberEvaluation(5, true), result);
  }

  @Test
  @DisplayName("Can evaluate calc expression with length value")
  public void canEvaluateCalcExpressionWithLengthValue() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, length(5));
    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(5), result);
  }

  @Test
  @DisplayName("Can add two numbers in calc expression")
  public void canAddTwoNumbersInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncDouble(
      CalcType.ADD,
      new CalcNumber(5, true),
      new CalcNumber(5, true)));
    
    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcNumberEvaluation(10, true), result);
  }

  @Test
  @DisplayName("Can add two lengths in calc expression")
  public void canAddTwoLengthsInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncDouble(
      CalcType.ADD,
      length(5),
      length(5)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(10), result);
  }

  @Test
  @DisplayName("Can divide length by number in calc expression")
  public void canDivideLengthByNumberInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncDouble(
      CalcType.DIV,
      length(10),
      new CalcNumber(2, true)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(5), result);
  }

  @Test
  @DisplayName("Can not divide number by length in calc expression")
  public void cannotDivideNumberByLengthInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncDouble(
      CalcType.DIV,
      new CalcNumber(10, true),
      length(2)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertTrue(result.isFailure());
  }

  @Test
  @DisplayName("Can multiply number by length in calc expression")
  public void canMultiplyNumberByLengthInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncDouble(
      CalcType.MUL,
      new CalcNumber(5, true),
      length(2)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(10), result);
  }

  @Test
  @DisplayName("Can evaluate nested calc expression")
  public void canEvaluateNestedCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncDouble(
      CalcType.ADD,
      new CalcFuncDouble(
        CalcType.MUL,
        new CalcNumber(2, true),
        length(3)),
      length(5)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(11), result);
  }

  @Test
  @DisplayName("Can evaluate max of numbers in calc expression")
  public void canEvaluateMaxOfNumbersInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncMany(
      CalcType.MAX, List.of(
        new CalcNumber(5, true),
        new CalcNumber(10, true),
        new CalcNumber(7, true))));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcNumberEvaluation(10, true), result);
  }

  @Test
  @DisplayName("Can evaluate min of lengths in calc expression")
  public void canEvaluateMinOfLengthsInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncMany(
      CalcType.MIN, List.of(
        length(5),
        length(10),
        length(7))));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(5), result);
  }

  @Test
  @DisplayName("Can not evaluate max of number and length in calc expression")
  public void cannotEvaluateMaxOfNumberAndLengthInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcFuncMany(
      CalcType.MAX, List.of(
        new CalcNumber(5, true),
        length(10))));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertTrue(result.isFailure());
  }

  @Test
  @DisplayName("Can clamp number in calc expression")
  public void canClampNumberInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcClampFunc(
      CalcType.CLAMP,
      new CalcNumber(5, true),
      new CalcNumber(10, true),
      new CalcNumber(7, true)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcNumberEvaluation(7, true), result);
  }

  @Test
  @DisplayName("Can clamp number and none in calc expression")
  public void canClampNumberAndNoneInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcClampFunc(
      CalcType.CLAMP,
      new CalcNumber(5, true),
      new CalcNumber(10, true),
      CSSValue.NONE));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcNumberEvaluation(10, true), result);
  }

  @Test
  @DisplayName("Can clamp length in calc expression")
  public void canClampLengthInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcClampFunc(
      CalcType.CLAMP,
      length(5),
      length(10),
      length(7)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertEquals(new CalcLengthPercentageEvaluation(7), result);
  }

  @Test
  @DisplayName("Can not clamp length by number in calc expression")
  public void cannotClampLengthByNumberInCalcExpression() {
    CalcValue calcValue = new CalcFuncSingle(CalcValue.CalcType.CALC, new CalcClampFunc(
      CalcType.CLAMP,
      new CalcNumber(5, true),
      length(10),
      new CalcNumber(7, true)));

    CalcEvaluation result = CalcInterpreter.evaluateNode(calcValue, this::fallback);
    Assertions.assertTrue(result.isFailure());
  }

  // TODO: Test the other less common functions

  private CSSValue length(float i) {
    return new LengthValue(i, false, LengthType.PX);
  }

  private CalcEvaluation fallback(CSSValue value) {
    if (value instanceof LengthValue lengthValue) {
      return new CalcLengthPercentageEvaluation(lengthValue.value().floatValue());
    }

    return CalcEvaluation.FAILURE;
  }

}
