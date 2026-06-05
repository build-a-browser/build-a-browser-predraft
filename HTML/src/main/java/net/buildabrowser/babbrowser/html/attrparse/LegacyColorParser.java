package net.buildabrowser.babbrowser.html.attrparse;

import java.util.Map;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;

public final class LegacyColorParser {
  
  private LegacyColorParser() {}

  // Result packed in #RGB format
  public static int parseLegacyColor(
    String input, Map<String, Integer> colors
  ) {
    if (input.isEmpty()) return -1;
    input = input.toLowerCase().trim();
    if (input.equals("transparent")) return -1;
    Integer matchedColor = colors.get(input);
    if (matchedColor != null) return matchedColor & 0x00FFFFFF;
    int comp3Color = parse3ComponentLegacyColor(input);
    if (comp3Color != -1) return comp3Color;
    // Fast path (not listed in spec, should behave the same)
    int comp6Color = parse6ComponentLegacyColor(input);
    if (comp6Color != -1) return comp6Color;

    StringBuilder colorBuilder = new StringBuilder(input);
    int numCodePoints = replaceAndCount(colorBuilder, ch -> ch <= 255, "00");

    if (numCodePoints > 128) {
      colorBuilder.setLength(colorBuilder.offsetByCodePoints(0, 128));
    }
    if (colorBuilder.codePointAt(0) == '#') {
      colorBuilder.delete(0, Character.charCount('#'));
    }

    numCodePoints = replaceAndCount(colorBuilder, ASCIIUtil::isHexDigit, "0");
    while (
      numCodePoints == 0
      || numCodePoints % 3 != 0
    ) {
      colorBuilder.append('0');
      numCodePoints++;
    }

    int length = numCodePoints / 3;
    int firstMark = colorBuilder.offsetByCodePoints(0, length);
    int secondMark = colorBuilder.offsetByCodePoints(firstMark, length);
    StringBuilder comp1 = new StringBuilder(
      colorBuilder.substring(0, firstMark));
    StringBuilder comp2 = new StringBuilder(
      colorBuilder.substring(firstMark, secondMark));
    StringBuilder comp3 = new StringBuilder(
      colorBuilder.substring(secondMark, colorBuilder.length()));
    if (length > 8) {
      comp1.delete(0, comp1.offsetByCodePoints(0, length - 8));
      comp2.delete(0, comp2.offsetByCodePoints(0, length - 8));
      comp3.delete(0, comp3.offsetByCodePoints(0, length - 8));
      length = 8;
    }
    int zeroLen = Character.charCount('0');
    while (
      length > 2
      && comp1.codePointAt(0) == '0'
      && comp2.codePointAt(0) == '0'
      && comp3.codePointAt(0) == '0'
    ) {
      comp1.delete(0, zeroLen);
      comp2.delete(0, zeroLen);
      comp3.delete(0, zeroLen);
      length--;
    }

    System.out.println(comp1 + " " + comp2 + " " + comp3);
    int red1 = ASCIIUtil.hexValue(comp1.codePointAt(0));
    int green1 = ASCIIUtil.hexValue(comp2.codePointAt(0));
    int blue1 = ASCIIUtil.hexValue(comp3.codePointAt(0));
    if (length == 1) {
      return newColor(red1, green1, blue1);
    }

    int red = red1 * 16 + ASCIIUtil.hexValue(comp1.codePointAt(1));
    int green = green1 * 16 + ASCIIUtil.hexValue(comp2.codePointAt(1));
    int blue = blue1 * 16 + ASCIIUtil.hexValue(comp3.codePointAt(1));
    return newColor(red, green, blue);
  }

  private static int replaceAndCount(
    StringBuilder colorBuilder,
    CharFilter charFilter,
    String replacement
  ) {
    int replacementLen = replacement.length();
    int replacementCodePoints = replacement.codePointCount(0, replacementLen);
    int numCodePoints = 0;
    for (int i = 0; i < colorBuilder.length();) {
      int ch = colorBuilder.codePointAt(i);
      if (!charFilter.isValid(ch)) {
        colorBuilder.replace(i, i + replacementLen, replacement);
        i += replacementCodePoints;
        numCodePoints += replacementCodePoints;
      } else {
        i += Character.charCount(ch);
        numCodePoints += 1;
      }
    }
    return numCodePoints;
  }

  private static int parse3ComponentLegacyColor(String input) {
    if (!(
      input.length() == 4
      && input.codePointAt(0) == '#'
      && ASCIIUtil.isHexDigit(input.codePointAt(1))
      && ASCIIUtil.isHexDigit(input.codePointAt(2))
      && ASCIIUtil.isHexDigit(input.codePointAt(3))
    )) return -1;

    return newColor(
      ASCIIUtil.hexValue(input.codePointAt(1)) * 17,
      ASCIIUtil.hexValue(input.codePointAt(2)) * 17,
      ASCIIUtil.hexValue(input.codePointAt(3)) * 17
    );
  }

  private static int parse6ComponentLegacyColor(String input) {
    if (!(
      input.length() == 7
      && input.codePointAt(0) == '#'
      && ASCIIUtil.isHexDigit(input.codePointAt(1))
      && ASCIIUtil.isHexDigit(input.codePointAt(2))
      && ASCIIUtil.isHexDigit(input.codePointAt(3))
      && ASCIIUtil.isHexDigit(input.codePointAt(4))
      && ASCIIUtil.isHexDigit(input.codePointAt(5))
      && ASCIIUtil.isHexDigit(input.codePointAt(6))
    )) return -1;

    return newColor(
      ASCIIUtil.hexValue(input.codePointAt(1)) * 16
        + ASCIIUtil.hexValue(input.codePointAt(2)),
      ASCIIUtil.hexValue(input.codePointAt(3)) * 16
        + ASCIIUtil.hexValue(input.codePointAt(4)),
      ASCIIUtil.hexValue(input.codePointAt(5)) * 16
        + ASCIIUtil.hexValue(input.codePointAt(6))
    );
  }

  private static int newColor(int r, int g, int b) {
    return (r << 16) + (g << 8) + b;
  }

  private static interface CharFilter {
    boolean isValid(int ch);
  }

}
