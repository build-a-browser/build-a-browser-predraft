package net.buildabrowser.babbrowser.css.tokenizer.imp;

public final class TokenizerUtil {
  
  private TokenizerUtil() {}

  private static boolean isDigit(int ch) {
    return ch >= '0' && ch <= '9';
  }

  private static boolean isUppercaseLetter(int ch) {
    return ch >= 'A' && ch <= 'Z';
  }

  private static boolean isLowercaseLetter(int ch) {
    return ch >= 'a' && ch <= 'z';
  }

  public static boolean isLetter(int ch) {
    return isUppercaseLetter(ch) || isLowercaseLetter(ch);
  }

  public static boolean isNonAsciiCodePoint(int ch) {
    return ch >= 0x80;
  }

  public static boolean isIdentStartCodePoint(int ch) {
    return isLetter(ch) || isNonAsciiCodePoint(ch) || ch == '_';
  }

  public static boolean isIdentCodePoint(int ch) {
    return isIdentStartCodePoint(ch) || isDigit(ch) || ch == '-';
  }

}
