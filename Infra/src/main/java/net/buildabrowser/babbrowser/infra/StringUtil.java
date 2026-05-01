package net.buildabrowser.babbrowser.infra;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;

public final class StringUtil {
  
  private StringUtil() {}

  public static String collectCodePoints(String input, Condition condition, int[] position) {
    StringBuilder result = new StringBuilder();
    while (position[0] < input.length()) {
      int codepoint = input.codePointAt(position[0]);
      if (!condition.matches(codepoint)) break;
      result.appendCodePoint(codepoint);
      position[0]++;
    }

    return result.toString();
  }

  public static void skipASCIIWhitespace(String input, int[] position) {
    collectCodePoints(input, ch -> ASCIIUtil.isWhitespace(ch), position);
  }

  public static interface Condition {
    
    boolean matches(int codepoint);
    
  }

}
