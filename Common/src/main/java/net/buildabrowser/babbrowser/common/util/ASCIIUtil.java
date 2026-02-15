package net.buildabrowser.babbrowser.common.util;

public final class ASCIIUtil {
  
  private ASCIIUtil() {}

  public static boolean isAlpha(int ch) {
    return
      (ch >= 'a' && ch <= 'z')
      || (ch >= 'A' && ch <= 'Z');
  }

  public static boolean isUpper(int ch) {
    return ch >= 'A' && ch <= 'Z';
  }

  public static boolean isLower(int ch) {
    return ch >= 'a' && ch <= 'z';
  }

  public static boolean isNonAsciiCodePoint(int ch) {
    return ch >= 0x80;
  }

  public static int toLower(int ch) {
    if (ch >= 'A' && ch <= 'Z') {
      return ch - 0x20;
    }

    return ch;
  }

  public static boolean isHexDigit(int ch) {
    return hexValue(ch) != -1;
  }

  public static boolean isDigit(int ch) {
    return ch >= '0' && ch <='9';
  }

  public static int hexValue(int ch) {
    if (ch >= '0' && ch <='9') {
      return ch - '0';
    } else if (ch >= 'a' && ch <= 'f') {
      return ch - 'a' + 10;
    } else if (ch >= 'A' && ch <= 'F') {
      return ch - 'A' + 10;
    }

    return -1;
  }

}
