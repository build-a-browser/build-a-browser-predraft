package net.buildabrowser.babbrowser.css.engine.property.size;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class SizeParser implements PropertyValueParser {

  private static final CSSFailure NO_VALID_RESULT = new CSSFailure("No valid result...");
  private static final CSSFailure INVALID_LENGTH_TYPE = new CSSFailure("Unknown length type!");
  private static final CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token");

  private static final Map<String, LengthType> LENGTH_TYPES = Map.of(
    "em", LengthType.EM,
    "ex", LengthType.EX,
    "in", LengthType.IN,
    "cm", LengthType.CM,
    "mm", LengthType.MM,
    "pt", LengthType.PT,
    "pc", LengthType.PC,
    "px", LengthType.PX
  );

  private final boolean allowNone;
  private final boolean allowAuto;
  private final boolean allowPercent;
  private final CSSProperty property;

  public SizeParser(boolean allowNone, boolean allowAuto, boolean allowPercent, CSSProperty property) {
    this.allowNone = allowNone;
    this.allowAuto = allowAuto;
    this.allowPercent = allowPercent;
    this.property = property;
  }

  public SizeParser(boolean allowNone, boolean allowAuto, CSSProperty property) {
    this(allowNone, allowAuto, true, property);
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = parseInternal(stream, activeStyles);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) {
      return EXPECTED_EOF;
    }

    activeStyles.setProperty(property, result);
    return result;
  }

  @Override
  public CSSProperty relatedProperty() {
    return property;
  }

  public CSSValue parseInternal(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    Token token = stream.read();
    if (
      allowNone
      && token instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      return CSSValue.NONE;
    } else if (
      allowAuto
      && token instanceof IdentToken identToken
      && identToken.value().equals("auto")
    ) {
      return CSSValue.AUTO;
    } else if (token instanceof PercentageToken percentageToken && allowPercent) {
      return PercentageValue.create(percentageToken.value());
    } else if (token instanceof DimensionToken dimensionToken) {
      LengthType lengthType = dimensionToken.dimension() == null ? null :
        LENGTH_TYPES.get(dimensionToken.dimension());
      if (lengthType == null && !dimensionToken.value().equals((Number) 0)) {
        return INVALID_LENGTH_TYPE;
      }

      return LengthValue.create(
        dimensionToken.value(),
        dimensionToken.isInteger(),
        lengthType);
    } else if (
      token instanceof NumberToken numberToken
      && numberToken.isInteger()
      && numberToken.value().intValue() == 0
    ) {
      return LengthValue.create(0, true, null);
    } else {
      return NO_VALID_RESULT;
    }
  }

  public static SizeParser forMargin(CSSProperty unit) {
    return new SizeParser(false, true, unit);
  }

  public static SizeParser forPadding(CSSProperty unit) {
    return new SizeParser(false, false, unit);
  }

  public static SizeParser forPosition(CSSProperty unit) {
    return new SizeParser(false, true, unit);
  }

  public static SizeParser forNormal(CSSProperty unit) {
    return new SizeParser(false, true, unit);
  }

  public static SizeParser forMin(CSSProperty unit) {
    return new SizeParser(false, false, unit);
  }

  public static SizeParser forMax(CSSProperty unit) {
    return new SizeParser(true, false, unit);
  }
  
}
