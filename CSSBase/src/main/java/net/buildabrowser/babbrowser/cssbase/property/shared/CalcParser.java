package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
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
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class CalcParser implements PropertyValueParser {
  
  private static final Map<String, CalcKeyword> KEYWORD_MAP = Map.of(
    "e", CalcKeyword.E,
    "pi", CalcKeyword.PI,
    "infinity", CalcKeyword.INFINITY,
    "-infinity", CalcKeyword.NEG_INFINITY,
    "NaN", CalcKeyword.NaN
  );

  private static final Map<String, RoundingStrategy> ROUNDING_STRATEGY_MAP = Map.of(
    "nearest", RoundingStrategy.NEAREST,
    "up", RoundingStrategy.UP,
    "down", RoundingStrategy.DOWN,
    "to-zero", RoundingStrategy.TO_ZERO,
    "line-width", RoundingStrategy.LINE_WIDTH
  );

  private final Map<String, CalcEntry> CALC_TYPE_MAP = Map.ofEntries(
    regType("calc", CalcType.CALC, this::parseCalcSumF),
    regType("min", CalcType.MIN, this::parseCalcSumFList),
    regType("max", CalcType.MAX, this::parseCalcSumFList),
    regType("clamp", CalcType.CLAMP, this::parseClamp),
    regType("round", CalcType.ROUND, this::parseRound),
    regType("mod", CalcType.MOD, this::parseCalcSumFPair),
    regType("rem", CalcType.REM, this::parseCalcSumFPair),
    regType("sin", CalcType.SIN, this::parseCalcSumF),
    regType("cos", CalcType.COS, this::parseCalcSumF),
    regType("tan", CalcType.TAN, this::parseCalcSumF),
    regType("asin", CalcType.ASIN, this::parseCalcSumF),
    regType("acos", CalcType.ACOS, this::parseCalcSumF),
    regType("atan", CalcType.ATAN, this::parseCalcSumF),
    regType("atan2", CalcType.ATAN2, this::parseCalcSumFPair),
    regType("pow", CalcType.POW, this::parseCalcSumFPair),
    regType("sqrt", CalcType.SQRT, this::parseCalcSumF),
    regType("hypot", CalcType.HYPOT, this::parseCalcSumFList),
    regType("log", CalcType.LOG, this::parseLog),
    regType("exp", CalcType.EXP, this::parseCalcSumF),
    regType("abs", CalcType.ABS, this::parseCalcSumF),
    regType("sign", CalcType.SIGN, this::parseCalcSumF)
  );

  private CSSProperty relatedProperty;
  private PropertyValueParser innerParser;

  public CalcParser(CSSProperty relatedProperty, PropertyValueParser innerParser) {
    this.relatedProperty = relatedProperty;
    this.innerParser = innerParser;
  }

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof FunctionValue functionValue
      && CALC_TYPE_MAP.containsKey(functionValue.name())
    ) {
      stream.read();
      CalcEntry entry = CALC_TYPE_MAP.get(functionValue.name());
      CSSTokenStream childStream = ListCSSTokenStream.createWithSkippedWhitespace(
        stream.source(), functionValue.value());
      CSSValue result = entry.parser().parse(childStream, entry.type());
      if (!(childStream.peek() instanceof EOFToken)) {
        return CSSFailure.EXPECTED_EOF;
      }
      return result;
    }

    return innerParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }

  private CSSValue parseCalcSumF(CSSTokenStream stream, CalcType type) throws IOException {
    CSSValue value = parseCalcSum(stream);
    if (value.isFailure()) return value;

    return new CalcFuncSingle(type, value);
  }

  private CSSValue parseCalcSumFPair(CSSTokenStream stream, CalcType type) throws IOException {
    CSSValue firstValue = parseCalcSum(stream);
    if (firstValue.isFailure()) return firstValue;

    if (!(stream.read() instanceof CommaToken)) {
      return CSSFailure.EXPECTED_COMMA;
    }

    CSSValue secondValue = parseCalcSum(stream);
    if (secondValue.isFailure()) return secondValue;

    return new CalcFuncDouble(type, firstValue, secondValue);
  }

  private CSSValue parseCalcSumFList(CSSTokenStream stream, CalcType type) throws IOException {
    List<CSSValue> values = new ArrayList<>(2);

    CSSValue value = parseCalcSum(stream);
    if (value.isFailure()) return value;
    values.add(value);

    while (stream.peek() instanceof CommaToken) {
      stream.read();

      value = parseCalcSum(stream);
      if (value.isFailure()) return value;
      values.add(value);
    }

    return new CalcFuncMany(type, values);
  }

  private CSSValue parseClamp(CSSTokenStream stream, CalcType type) throws IOException {
    CSSValue minValue;
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      stream.read();
      minValue = CSSValue.NONE;
    } else {
      minValue = parseCalcSum(stream);
    }
    if (minValue.isFailure()) return minValue;

    if (!(stream.read() instanceof CommaToken)) {
      return CSSFailure.EXPECTED_COMMA;
    }

    CSSValue idealValue = parseCalcSum(stream);
    if (idealValue.isFailure()) return idealValue;

    if (!(stream.read() instanceof CommaToken)) {
      return CSSFailure.EXPECTED_COMMA;
    }

    CSSValue maxValue;
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      stream.read();
      maxValue = CSSValue.NONE;
    } else {
      maxValue = parseCalcSum(stream);
    }
    if (maxValue.isFailure()) return maxValue;

    return new CalcClampFunc(type, minValue, idealValue, maxValue);
  }

  private CSSValue parseRound(CSSTokenStream stream, CalcType type) throws IOException {
    RoundingStrategy roundingStrategy = RoundingStrategy.NEAREST;
    if (
      stream.peek() instanceof IdentToken identToken
      && ROUNDING_STRATEGY_MAP.containsKey(identToken.value())
    ) {
      stream.read();
      roundingStrategy = ROUNDING_STRATEGY_MAP.get(identToken.value());
      if (!(stream.read() instanceof CommaToken)) {
        return CSSFailure.EXPECTED_COMMA;
      }
    }

    CSSValue a = parseCalcSum(stream);
    if (a.isFailure()) return a;
    
    CSSValue b = null;
    if (stream.peek() instanceof CommaToken) {
      stream.read();
      b = parseCalcSum(stream);
      if (b.isFailure()) return b;
    }

    return new CalcRoundFunc(type, roundingStrategy, a, b);
  }

  private CSSValue parseLog(CSSTokenStream stream, CalcType type) throws IOException {
    CSSValue firstValue = parseCalcSum(stream);
    if (firstValue.isFailure()) return firstValue;

    CSSValue secondValue = null;
    if (stream.peek() instanceof CommaToken) {
      stream.read();

      secondValue = parseCalcSum(stream);
      if (secondValue.isFailure()) return secondValue;
    }

    return new CalcLogFunc(type, firstValue, secondValue);
  }

  private CSSValue parseCalcSum(CSSTokenStream stream) throws IOException {
    CSSValue firstProduct = parseCalcProduct(stream);
    if (firstProduct.isFailure()) return firstProduct;

    CSSValue currentSum = firstProduct;
    while (
      stream.peek() instanceof DelimToken delimToken
      && (delimToken.ch() == '+' || delimToken.ch() == '-')
    ) {
      stream.read();
      CalcType type = delimToken.ch() == '+' ? CalcType.ADD : CalcType.SUB;
      CSSValue nextProduct = parseCalcProduct(stream);
      if (nextProduct.isFailure()) return nextProduct;
      currentSum = new CalcFuncDouble(type, currentSum, nextProduct);
    }

    return currentSum;
  }

  private CSSValue parseCalcProduct(CSSTokenStream stream) throws IOException {
    CSSValue firstValue = parseCalcValue(stream);
    if (firstValue.isFailure()) return firstValue;

    CSSValue currentProduct = firstValue;
    while (
      stream.peek() instanceof DelimToken delimToken
      && (delimToken.ch() == '*' || delimToken.ch() == '/')
    ) {
      stream.read();
      CalcType type = delimToken.ch() == '*' ? CalcType.MUL : CalcType.DIV;
      CSSValue nextValue = parseCalcValue(stream);
      if (nextValue.isFailure()) return nextValue;
      currentProduct = new CalcFuncDouble(type, currentProduct, nextValue);
    }

    return currentProduct;
  }

  private CSSValue parseCalcValue(CSSTokenStream stream) throws IOException {
    // This differs a bit from the spec in that it delegates back to parse
    // instead of checking dimension | percentage
    if (
      stream.peek() instanceof SimpleBlock blockValue
      && blockValue.type() instanceof LParenToken
    ) {
      stream.read();
      CSSTokenStream childStream = ListCSSTokenStream.createWithSkippedWhitespace(
        stream.source(), blockValue.value());
      CSSValue sum = parseCalcSum(childStream);

      if (sum.isFailure()) return sum;
      if (!(
        childStream.peek() instanceof EOFToken
      )) return CSSFailure.EXPECTED_EOF;
      
      return sum;
    } else if (stream.peek() instanceof NumberToken numberToken) {
      stream.read();
      return new CalcNumber(numberToken.value(), numberToken.isInteger());
    } else if (
      stream.peek() instanceof IdentToken identToken
      && KEYWORD_MAP.containsKey(identToken.value())
    ) {
      stream.read();
      return KEYWORD_MAP.get(identToken.value());
    } else {
      return parse(stream);
    }
  }

  private Map.Entry<String, CalcEntry> regType(String name, CalcType type, CalcSubParser parser) {
    return Map.entry(name, new CalcEntry(type, parser));
  }

  private static record CalcEntry(CalcType type, CalcSubParser parser) {}

  private static interface CalcSubParser {
    CSSValue parse(CSSTokenStream stream, CalcType type) throws IOException;
  }

}
