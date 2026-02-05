package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class RGBColorParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_FUNCTION = new CSSFailure("Expect an rgb function");
  private static final CSSFailure EXPECTED_NUMBER = new CSSFailure("Expect a number token");
  private static final CSSFailure EXPECTED_PERCENTAGE = new CSSFailure("Expect a percentage token");
  private static final CSSFailure EXPECTED_COMMA = new CSSFailure("Expect a comma token");

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (!(
      stream.read() instanceof FunctionValue function
      && (function.name().equals("rgb") || function.name().equals("rgba"))
    )) {
      return EXPECTED_FUNCTION;
    }

    SeekableCSSTokenStream childStream = ListCSSTokenStream.createWithSkippedWhitespace(function.value());

    return PropertyValueParserUtil.parseLongest(childStream,
      this::parseLegacyColor,
      this::parseModernColor);
  }

  public CSSValue parseLegacyColor(SeekableCSSTokenStream stream) throws IOException {
    boolean isPercent = stream.peek() instanceof PercentageToken;

    CSSFailure failure = checkLegacyColorComponent(stream.peek(), isPercent);
    if (failure != null) return failure;
    int redComponent = parseLegacyColorComponent(stream.read(), isPercent);
    if (!(stream.read() instanceof CommaToken)) return EXPECTED_COMMA;

    failure = checkLegacyColorComponent(stream.peek(), isPercent);
    if (failure != null) return failure;
    int greenComponent = parseLegacyColorComponent(stream.read(), isPercent);
    if (!(stream.read() instanceof CommaToken)) return EXPECTED_COMMA;

    failure = checkLegacyColorComponent(stream.peek(), isPercent);
    if (failure != null) return failure;
    int blueComponent = parseLegacyColorComponent(stream.read(), isPercent);

    int alphaComponent = 255;
    if (stream.peek() instanceof CommaToken) {
      stream.read();
      Token alphaToken = stream.read();
      if (alphaToken instanceof PercentageToken percentageToken) {
        alphaComponent = Math.clamp((int) (percentageToken.value().floatValue() / 100 * 255), 0, 255);
      } else if (alphaToken instanceof NumberToken numberToken) {
        alphaComponent = Math.clamp((int) (numberToken.value().floatValue() * 255), 0, 255);
      } else {
        return EXPECTED_NUMBER;
      }
    }

    if (!(stream.peek() instanceof EOFToken)) {
      return CSSFailure.EXPECTED_EOF;
    }

    return SRGBAColor.create(redComponent, greenComponent, blueComponent, alphaComponent);
  }

  public CSSValue parseModernColor(SeekableCSSTokenStream stream) throws IOException {
    CSSFailure failure = checkModernColorComponent(stream.peek());
    if (failure != null) return failure;
    int redComponent = parseModernColorComponent(stream.read());

    failure = checkModernColorComponent(stream.peek());
    if (failure != null) return failure;
    int greenComponent = parseModernColorComponent(stream.read());

    failure = checkModernColorComponent(stream.peek());
    if (failure != null) return failure;
    int blueComponent = parseModernColorComponent(stream.read());

    if (stream.peek() instanceof EOFToken) {
      return SRGBAColor.create(redComponent, greenComponent, blueComponent, 255);
    }

    int alphaComponent = 255;
    if (stream.peek() instanceof DelimToken delimToken && delimToken.ch() == '/') {
      stream.read();
    }

    Token alphaToken = stream.read();
    if (alphaToken instanceof PercentageToken percentageToken) {
      alphaComponent = Math.clamp((int) (percentageToken.value().floatValue() / 100 * 255), 0, 255);
    } else if (alphaToken instanceof NumberToken numberToken) {
      alphaComponent = Math.clamp((int) (numberToken.value().floatValue() * 255), 0, 255);
    } else if (
      alphaToken instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      alphaComponent = 255;
    } else {
      return EXPECTED_NUMBER;
    }

    if (!(stream.peek() instanceof EOFToken)) {
      return CSSFailure.EXPECTED_EOF;
    }

    return SRGBAColor.create(redComponent, greenComponent, blueComponent, alphaComponent);
  }

  private CSSFailure checkLegacyColorComponent(Token token, boolean isPercent) {
    if (isPercent) return token instanceof PercentageToken ? null : EXPECTED_PERCENTAGE;
    return token instanceof NumberToken ? null : EXPECTED_NUMBER;
  }

  private int parseLegacyColorComponent(Token token, boolean isPercent) {
    if (isPercent) return Math.clamp((int) (((PercentageToken) token).value().floatValue() / 100 * 255), 0, 255);
    return Math.clamp((int) (((NumberToken) token).value().floatValue()), 0, 255);
  }

  private CSSFailure checkModernColorComponent(Token token) {
    if (
      token instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) return null;
    return token instanceof PercentageToken || token instanceof NumberToken ? null : EXPECTED_NUMBER;
  }

  private int parseModernColorComponent(Token token) {
    if (token instanceof PercentageToken percentageToken) {
      return Math.clamp((int) (percentageToken.value().floatValue() / 100 * 255), 0, 255);
    } else if (token instanceof NumberToken numberToken) {
      return Math.clamp((int) (numberToken.value().floatValue()), 0, 255);
    } else return 0;
  }
  
}
