package net.buildabrowser.babbrowser.infra;

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

  public static interface Condition {
    
    boolean matches(int codepoint);
    
  }

}
