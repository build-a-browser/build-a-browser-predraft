package net.buildabrowser.babbrowser.cssbase.property.size;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.shared.CalcParser;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class SizeParser implements PropertyValueParser {

  private static final SizeParser PURE_LENGTH_PERCENTAGE = new SizeParser(false, false, null);

  private static final CSSFailure NO_VALID_RESULT = new CSSFailure("No valid result...");
  private static final CSSFailure INVALID_LENGTH_TYPE = new CSSFailure("Unknown length type!");

  private static final Map<String, LengthType> LENGTH_TYPES = CommonUtil.mapOf(
    "em", LengthType.EM,
    "rem", LengthType.REM,
    "ex", LengthType.EX,
    "ch", LengthType.CH,

    "cm", LengthType.CM,
    "mm", LengthType.MM,
    "Q", LengthType.Q,
    "in", LengthType.IN,
    "pt", LengthType.PT,
    "pc", LengthType.PC,
    "px", LengthType.PX,

    "vw", LengthType.VW,
    "vh", LengthType.VH,
    "vmin", LengthType.VMIN,
    "vmax", LengthType.VMAX
  );

  private final CalcParser calcParser;

  private final boolean allowNone;
  private final boolean allowAuto;
  private final boolean allowPercent;
  private final boolean allowMinMax;
  private final boolean allowFitContent;
  private final CSSProperty property;

  public SizeParser(
    boolean allowNone,
    boolean allowAuto,
    boolean allowPercent,
    boolean allowMinMax,
    boolean allowFitContent,
    CSSProperty property
  ) {
    this.allowNone = allowNone;
    this.allowAuto = allowAuto;
    this.allowPercent = allowPercent;
    this.allowMinMax = allowMinMax;
    this.allowFitContent = allowFitContent;
    this.property = property;
    this.calcParser = new CalcParser(property, this::parseInner);
  }

  public SizeParser(
    boolean allowNone,
    boolean allowAuto,
    boolean allowPercent,
    boolean allowMinMax,
    CSSProperty property
  ) {
    this(
      allowNone, allowAuto, allowPercent,
      allowMinMax, allowMinMax, property);
  }

  public SizeParser(boolean allowNone, boolean allowAuto, CSSProperty property) {
    this(
      allowNone, allowAuto, true,
      false, false, property);
  }

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    Token token = stream.peek();
    if (
      allowNone
      && token instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      stream.read();
      return CSSValue.NONE;
    } else if (
      allowAuto
      && token instanceof IdentToken identToken
      && identToken.value().equals("auto")
    ) {
      stream.read();
      return CSSValue.AUTO;
    } else if (
      token instanceof NumberToken numberToken
      && numberToken.isInteger()
      && numberToken.value().intValue() == 0
    ) {
      stream.read();
      return LengthValue.create(0, true, null);
    } else {
      return calcParser.parse(stream);
    }
  }

  public CSSValue parseInner(CSSTokenStream stream) throws IOException {
    Token token = stream.read();
    if (token instanceof PercentageToken percentageToken && allowPercent) {
      return PercentageValue.create(percentageToken.value());
    } else if (token instanceof DimensionToken dimensionToken) {
      LengthType lengthType = LENGTH_TYPES.get(dimensionToken.dimension());
      if (lengthType == null) {
        return INVALID_LENGTH_TYPE;
      }

      return LengthValue.create(
        dimensionToken.value(),
        dimensionToken.isInteger(),
        lengthType);
    // TODO: The below should only be allowed in the calc-size variant
    // They will need tested once said exists
    } else if (
      allowMinMax
      && token instanceof IdentToken identToken
      && identToken.value().equals("min-content")
    ) {
      return SizeValue.MIN_CONTENT;
    } else if (
      allowMinMax
      && token instanceof IdentToken identToken
      && identToken.value().equals("max-content")
    ) {
      return SizeValue.MAX_CONTENT;
    } else if (
      allowMinMax
      && token instanceof IdentToken identToken
      && identToken.value().equals("stretch")
    ) {
      return SizeValue.STRETCH;
    } else if (
      allowMinMax
      && token instanceof IdentToken identToken
      && identToken.value().equals("fit-content")
    ) {
      return SizeValue.FIT_CONTENT;
    } else if (
      allowMinMax
      && token instanceof IdentToken identToken
      && identToken.value().equals("contain")
    ) {
      return SizeValue.CONTAIN;
    } else if (
      allowFitContent
      && token instanceof FunctionValue funcValue
      && funcValue.name().equals("fit-content")
    ) {
      return parseFitContent(stream, funcValue);
    } else {
      return NO_VALID_RESULT;
    }
  }

  @Override
  public CSSProperty relatedProperty() {
    return property;
  }

  private CSSValue parseFitContent(
    CSSTokenStream refStream, FunctionValue funcValue
  ) throws IOException {
    CSSTokenStream stream = ListCSSTokenStream.createWithSkippedWhitespace(
      refStream.source(), funcValue.value());
    CSSValue result = PURE_LENGTH_PERCENTAGE.parse(stream);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;

    return SizeValue.FitContent.create(result);
  }

  public static SizeParser forMargin(CSSProperty unit) {
    return new SizeParser(false, true, unit);
  }

  public static SizeParser forPadding(CSSProperty unit) {
    return new SizeParser(false, false, unit);
  }

  public static SizeParser forOutline(CSSProperty unit) {
    return new SizeParser(
      false, false, false, false, unit);
  }

  public static SizeParser forPosition(CSSProperty unit) {
    return new SizeParser(false, true, unit);
  }

  public static SizeParser forNormal(CSSProperty unit) {
    return new SizeParser(false, true, true, true, unit);
  }

  public static SizeParser forInset(CSSProperty unit) {
    return new SizeParser(false, true, unit);
  }

  public static SizeParser forMin(CSSProperty unit) {
    return new SizeParser(false, true, true, true, unit);
  }

  public static SizeParser forMax(CSSProperty unit) {
    return new SizeParser(true, false, true, true, unit);
  }
  
}
