package net.buildabrowser.babbrowser.renderer.event.util;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;

public final class MouseEventUtil {

  private MouseEventUtil() {}

  public static int determineTextMouseIndex(float mouseX, FontMetrics fontMetrics, String value) {
    int cursorX = 0;
    int charNum = 0;
    while (
      charNum < value.length()
      // TODO: Bad performance, but if it was done character-by-character
      // then it might be thrown off by kerning
      && valueWidth(fontMetrics, value, charNum) / 2
        + valueWidth(fontMetrics, value, charNum + 1) / 2
        <= mouseX
    ) {
      cursorX++;
      charNum += Character.charCount(value.codePointAt(charNum));
    }
    return cursorX;
  }

  private static float valueWidth(FontMetrics fontMetrics, String value, int charNum) {
    return fontMetrics.stringWidth(value.substring(0, charNum));
  }

}
