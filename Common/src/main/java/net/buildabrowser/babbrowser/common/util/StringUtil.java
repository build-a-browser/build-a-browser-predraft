package net.buildabrowser.babbrowser.common.util;

public final class StringUtil {
  
  private StringUtil() {}

  public static String stripWhitespace(String str) {
    // TODO: Correctly only remove HTML whitespace
    return str.trim();
  }

  public static String[] spaceSplit(String item) {
    int afterSpaceIndex = 0;
    int currentIndex = 0;
    int arrSize = 0;
    while (currentIndex < item.length()) {
      if (item.codePointAt(currentIndex) == ' ') {
        if (currentIndex != afterSpaceIndex) {
          arrSize++;
        }
        afterSpaceIndex = Character.offsetByCodePoints(item, currentIndex, 1);
      }
      currentIndex = Character.offsetByCodePoints(item, currentIndex, 1);
    }
    if (currentIndex != afterSpaceIndex) {
      arrSize++;
    }

    afterSpaceIndex = 0;
    currentIndex = 0;
    int arrIndex = 0;
    String[] strings = new String[arrSize];
    while (currentIndex < item.length()) {
      if (item.codePointAt(currentIndex) == ' ') {
        if (currentIndex != afterSpaceIndex) {
          strings[arrIndex++] = item.substring(afterSpaceIndex, currentIndex);
        }
        afterSpaceIndex = Character.offsetByCodePoints(item, currentIndex, 1);
      }
      currentIndex = Character.offsetByCodePoints(item, currentIndex, 1);
    }
    if (currentIndex != afterSpaceIndex) {
      strings[arrIndex++] = item.substring(afterSpaceIndex, currentIndex);
    }

    return strings;
  }

}
