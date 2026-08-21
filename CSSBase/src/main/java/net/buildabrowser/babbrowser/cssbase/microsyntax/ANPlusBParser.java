package net.buildabrowser.babbrowser.cssbase.microsyntax;

import java.io.IOException;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public final class ANPlusBParser {

  private ANPlusBParser() {}

  public static ANPlusB parse(
    CSSTokenStream stream
  ) throws IOException {
    ignoreWhitespace(stream);
    ANPlusB result = parseInner(stream);
    ignoreWhitespace(stream);
    return result;
  }

  private static ANPlusB parseInner(
    CSSTokenStream stream
  ) throws IOException {
    Token firstToken = stream.read();
    if (
      // odd
      firstToken instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("odd")
    ) {
      return ANPlusB.ODD;
    } else if (
      // even
      firstToken instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("even")
    ) {
      return ANPlusB.EVEN;
    } else if (
      // <integer>
      firstToken instanceof NumberToken numberToken
      && numberToken.isInteger()
    ) {
      return ANPlusB.create(0, numberToken.value().intValue());
    } else if (
      // <n-dimension>
      // <n-dimension> <signed-integer>
      // <n-dimension> ['+' | '-'] <signless-integer>
      firstToken instanceof DimensionToken dimensionToken
      && dimensionToken.isInteger()
      && dimensionToken.dimension().equalsIgnoreCase("n")
    ) {
      int a = dimensionToken.value().intValue();
      int b = parseOptionalIntegerExtension(stream);
      return ANPlusB.create(a, b);
    } else if (
      // +n
      // +n <signed-integer>
      // +n ['+' | '-'] <signless-integer>
      firstToken instanceof DelimToken delimToken
      && delimToken.ch() == '+'
      && stream.peek() instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("n")
    ) {
      stream.read();
      int a = 1;
      int b = parseOptionalIntegerExtension(stream);
      return ANPlusB.create(a, b);
    } else if (
      // n
      // n <signed-integer>
      // n ['+' | '-'] <signless-integer>
      firstToken instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("n")
    ) {
      int a = 1;
      int b = parseOptionalIntegerExtension(stream);
      return ANPlusB.create(a, b);
    } else if (
      // -n
      // -n <signed-integer>
      // -n ['+' | '-'] <signless-integer>
      firstToken instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("-n")
    ) {
      int a = -1;
      int b = parseOptionalIntegerExtension(stream);
      return ANPlusB.create(a, b);
    } else if (
      // <ndashdigit-dimension>
      firstToken instanceof DimensionToken dimensionToken
      && isNDashDigitIdentStr(dimensionToken.dimension())
    ) {
      int a = dimensionToken.value().intValue();
      int b = Integer.valueOf(dimensionToken.dimension().substring(1));
      return ANPlusB.create(a, b);
    } else if (
      // +<ndashdigit-ident>
      firstToken instanceof DelimToken delimToken
      && delimToken.ch() == '+'
      && stream.peek() instanceof IdentToken identToken
      && isNDashDigitIdentStr(identToken.value())
    ) {
      stream.read();
      int a = 1;
      int b = Integer.valueOf(identToken.value().substring(1));
      return ANPlusB.create(a, b);
    } else if (
      // <dashndashdigit-ident>
      firstToken instanceof IdentToken identToken
      && identToken.value().length() > 3
      && identToken.value().codePointAt(0) == '-'
      && isNDashDigitIdentStr(identToken.value().substring(1))
    ) {
      stream.read();
      int a = -1;
      int b = Integer.valueOf(identToken.value().substring(2));
      return ANPlusB.create(a, b);
    } else if (
      // <ndash-dimension> <signless-integer>
      firstToken instanceof DimensionToken dimensionToken
      && dimensionToken.isInteger()
      && dimensionToken.dimension().equalsIgnoreCase("n-")
    ) {
      int a = dimensionToken.value().intValue();
      Integer b = parseSignlessInteger(stream);
      if (b == null) return null;
      return ANPlusB.create(a, -b);
    } else if (
      // +n- <signless-integer>
      firstToken instanceof DelimToken delimToken
      && delimToken.ch() == '+'
      && stream.peek() instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("n-")
    ) {
      stream.read();
      int a = 1;
      Integer b = parseSignlessInteger(stream);
      if (b == null) return null;
      return ANPlusB.create(a, -b);
    } else if (
      // n- <signless-integer>
      firstToken instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("n-")
    ) {
      int a = 1;
      Integer b = parseSignlessInteger(stream);
      if (b == null) return null;
      return ANPlusB.create(a, -b);
    } else if (
      // -n- <signless-integer>
      firstToken instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("-n-")
    ) {
      int a = -1;
      Integer b = parseSignlessInteger(stream);
      if (b == null) return null;
      return ANPlusB.create(a, -b);
    } else {
      return null;
    }
  }

  private static void ignoreWhitespace(
    CSSTokenStream stream
  ) throws IOException {
    while (stream.peek() instanceof WhitespaceToken) {
      stream.read();
    }
  }

  private static boolean isNDashDigitIdentStr(String dimension) {
    if (dimension.length() < 3) return false;

    Integer intValue = CommonUtil.tryOrNull(
      () -> Integer.valueOf(dimension.substring(1)));
    boolean isValidInt = intValue != null;
    return
      dimension.toLowerCase().startsWith("n-")
      && isValidInt;
  }

  private static int parseOptionalIntegerExtension(
    CSSTokenStream stream
  ) throws IOException {
    ignoreWhitespace(stream);
    if (
      stream.peek() instanceof DelimToken delimToken
      && (delimToken.ch() == '+' || delimToken.ch() == '-')
    ) {
      int mark = stream.mark();
      stream.read();
      ignoreWhitespace(stream);
      int multiplier = delimToken.ch() == '-' ? -1 : 1;
      Integer value = parseSignlessInteger(stream);
      if (value == null) {
        stream.restoreMark(mark);
        return 0;
      }

      stream.discardMark();
      return multiplier * value;
    } else if (
      stream.peek() instanceof NumberToken numberToken
      && numberToken.isInteger()
      && numberToken.isSigned()
    ) {
      stream.read();
      return numberToken.value().intValue();
    }

    return 0;
  }

  private static Integer parseSignlessInteger(
    CSSTokenStream stream
  ) throws IOException {
    ignoreWhitespace(stream);
    if (!(
      stream.read() instanceof NumberToken numberToken
      && numberToken.isInteger()
      && !numberToken.isSigned()
    )) return null;

    return numberToken.value().intValue();
  }

}

