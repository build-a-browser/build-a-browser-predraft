package net.buildabrowser.babbrowser.cssbase.tokenizer.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;

public final class TokenizerUtil {
  
  private TokenizerUtil() {}

  public static boolean isIdentStartCodePoint(int ch) {
    return ASCIIUtil.isAlpha(ch) || ASCIIUtil.isNonAsciiCodePoint(ch) || ch == '_';
  }

  public static boolean isIdentCodePoint(int ch) {
    return isIdentStartCodePoint(ch) || ASCIIUtil.isDigit(ch) || ch == '-';
  }

  public static boolean isValidEscape(CSSTokenizerInput stream) throws IOException {
    int ch1 = stream.read();
    int ch2 = stream.peek();
    stream.unread(ch1);

    return isValidEscape(ch1, ch2);
  }

  public static boolean isValidEscape(int ch1, int ch2) {
    if (ch1 != '\\') return false;
    if (ch2 == '\n') return false;
    return true;
  }

  public static boolean wouldStartAnIdentSequence(CSSTokenizerInput stream) throws IOException {
    int ch1 = stream.read();
    int ch2 = stream.read();
    int ch3 = stream.peek();
    stream.unread(ch2);
    stream.unread(ch1);

    return wouldStartAnIdentSequence(ch1, ch2, ch3);
  }

  public static boolean wouldStartAnIdentSequence(int ch1, int ch2, int ch3) {
    return switch (ch1) {
      case '-' ->
        isIdentStartCodePoint(ch2)
        || ch2 == '-'
        || isValidEscape(ch2, ch3);
      case '\\' -> isValidEscape(ch1, ch2);
      default -> isIdentStartCodePoint(ch1);
    };
  }

}
