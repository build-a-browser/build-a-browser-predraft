package net.buildabrowser.babbrowser.html.attrparse;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.infra.StringUtil;

public final class DimensionParser {

  private DimensionParser() {}

  public static DimensionParserResult parseDimension(String input) {
    int[] position = new int[1];
    StringUtil.skipASCIIWhitespace(input, position);
    if (
      position[0] >= input.length()
      || !ASCIIUtil.isDigit(input.codePointAt(position[0]))
    ) return null;
    String digits = StringUtil.collectCodePoints(
      input, ASCIIUtil::isDigit, position);
    float value = Integer.valueOf(digits);
    if (position[0] >= input.length()) {
      return DimensionParserResult.length(value);
    }
    if (input.codePointAt(position[0]) == '.') {
      position[0]++;
      if (
        position[0] >= input.length()
        || !ASCIIUtil.isDigit(input.codePointAt(position[0]))
      ) return currentDimensionValue(value, input, position);
      int divisor = 1;
      float digit = ASCIIUtil.digitValue(input.codePointAt(position[0]));
      while (true) {
        divisor *= 10;
        value += digit / divisor;
        position[0]++;
        if (position[0] >= input.length()) {
          return DimensionParserResult.length(value);
        }
        digit = ASCIIUtil.digitValue(input.codePointAt(position[0]));
        if (digit == -1) break;
      }
    }

    return currentDimensionValue(value, input, position);
  }

  private static DimensionParserResult currentDimensionValue(
    float value, String input, int[] position
  ) {
    if (position[0] > input.length()) {
      return DimensionParserResult.length(value);
    }
    if (input.codePointAt(position[0]) == '%') {
      return DimensionParserResult.percentage(value);  
    }

    return DimensionParserResult.length(value);
  }

  public static record DimensionParserResult(
    float number, boolean isPercent
  ) {

    public static DimensionParserResult length(float value) {
      return new DimensionParserResult(value, false);
    }

    public static DimensionParserResult percentage(float value) {
      return new DimensionParserResult(value, true);
    }

  }

}
