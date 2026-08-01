package net.buildabrowser.babbrowser.common.util;

public final class StringUtil {
  
  private StringUtil() {}

  public static String stripWhitespace(String str) {
    // TODO: Correctly only remove HTML whitespace
    return str.trim();
  }

  public static String[] spaceSplit(String item) {
    return chSplit(item, ' ');
  }

  public static String[] chSplit(String item, int ch) {
    int afterSpaceIndex = 0;
    int currentIndex = 0;
    int arrSize = 0;
    while (currentIndex < item.length()) {
      if (item.codePointAt(currentIndex) == ch) {
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
      if (item.codePointAt(currentIndex) == ch) {
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

  public static int countChar(String text, int ch) {
    int i = 0;
    int count = 0;
    while (i < text.length()) {
      if (text.codePointAt(i) == ch) {
        count++;
      }
      i = Character.offsetByCodePoints(text, i, 1);
    }

    return count;
  }

}
