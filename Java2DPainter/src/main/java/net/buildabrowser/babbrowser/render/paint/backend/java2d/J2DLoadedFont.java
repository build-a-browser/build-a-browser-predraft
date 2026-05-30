package net.buildabrowser.babbrowser.render.paint.backend.java2d;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;

import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader.FontOptions;

// Copied from SkijaLoadedFont, ported for J2D
public class J2DLoadedFont implements LoadedFont {

  private final Font[] rawFonts;
  private final J2DFontMetrics metrics;

  private final Font[] effectiveFontCache = new Font[256];

  public J2DLoadedFont(Font[] rawFonts, FontOptions options) {
    this.rawFonts = rawFonts;
    this.metrics = new J2DFontMetrics(new Canvas().getFontMetrics(rawFonts[0]), options);
  }

  @Override
  public FontMetrics metrics() {
    return this.metrics;
  }

  public void drawText(
    float x, float y, String text, Graphics g
  ) {
    if (text.isEmpty()) return;
    
    int windowStart = 0;
    float currentX = x;
    float adjustedY = y - metrics.ascent();
    while (windowStart < text.length()) {
      Font currentFont = glyphFont(text.codePointAt(windowStart));
      int windowEnd = endOfConsecutiveFontChars(text, windowStart);
      String windowText = text.substring(windowStart, windowEnd + 1);

      currentX += drawPartialText(windowText, currentX, adjustedY, currentFont, g);
      windowStart = windowEnd + 1;
    }
  }

  private float drawPartialText(
    String text, float x, float y, Font font, Graphics g
  ) {
    g.setFont(font);
    g.drawString(text, (int) x, (int) y);

    return g.getFontMetrics().stringWidth(text);
  }

  private int endOfConsecutiveFontChars(String text, int windowStart) {
    Font initialFont = glyphFont(text.codePointAt(windowStart));
    int windowEnd = windowStart + 1;
    while (windowEnd < text.length()) {
      Font font = glyphFont(text.codePointAt(windowEnd));
      if (font != initialFont) {
        return windowEnd - 1;
      }
      windowEnd++;
    }

    return windowEnd - 1;
  }

  private Font glyphFont(int codePoint) {
    // TODO: Look into caching codepoints outside of this, for better i18n performance
    if (
      codePoint < 256
      && effectiveFontCache[codePoint] != null
    ) {
      return effectiveFontCache[codePoint];
    }

    Font rawFont = glyphFontRaw(codePoint);
    if (codePoint < 256) {
      effectiveFontCache[codePoint] = rawFont;
    }

    return rawFont;
  }

  private Font glyphFontRaw(int codePoint) {
    for (Font rawFont: rawFonts) {
      if (rawFont.canDisplay(codePoint)) {
        return rawFont;
      }
    }

    // TODO: Scan all system fonts
    return rawFonts[0];
  }

}
