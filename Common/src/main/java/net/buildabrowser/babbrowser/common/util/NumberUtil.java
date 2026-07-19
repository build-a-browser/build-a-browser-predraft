package net.buildabrowser.babbrowser.common.util;

public final class NumberUtil {
  
  private NumberUtil() {}

  // Integer.valueOf creates a stack trace for invalid integers, which is slow
  public static Integer parseInteger(String valueStr) {
    if (valueStr.length() == 0) return null;
    
    int index = 0;
    int firstCodePoint = valueStr.codePointAt(0);
    boolean isNegative = firstCodePoint == '-';
    int value = 0;
    if (
      firstCodePoint == '+'
      || firstCodePoint == '-'
    ){
      index++;
    }

    if (index == valueStr.length()) {
      return null;
    }

    while (index < valueStr.length()) {
      int digit = ASCIIUtil.digitValue(valueStr.codePointAt(index++));
      if (digit == -1) return null;
      value *= 10;
      value += digit;
    }

    return value * (isNegative ? -1 : 1);
  }

}
