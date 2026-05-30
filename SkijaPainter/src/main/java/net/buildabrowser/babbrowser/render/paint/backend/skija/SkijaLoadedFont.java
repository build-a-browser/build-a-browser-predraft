package net.buildabrowser.babbrowser.render.paint.backend.skija;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextBlob;
import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader.FontOptions;

public class SkijaLoadedFont implements LoadedFont {

  private final Font[] rawFonts;
  private final SkijaFontMetrics metrics;

  private final Font[] effectiveFontCache = new Font[256];

  public SkijaLoadedFont(Font[] rawFonts, FontOptions options) {
    this.rawFonts = rawFonts;
    this.metrics = new SkijaFontMetrics(rawFonts, options);
  }

  @Override
  public FontMetrics metrics() {
    return this.metrics;
  }

  public void drawText(
    float x, float y, String text,
    Canvas canvas, Paint rawPaint
  ) {
    if (text.isEmpty()) return;
    
    int windowStart = 0;
    float currentX = x;
    float adjustedY = y - metrics.ascent();
    while (windowStart < text.length()) {
      Font currentFont = glyphFont(text.codePointAt(windowStart));
      int windowEnd = endOfConsecutiveFontChars(text, windowStart);
      String windowText = text.substring(windowStart, windowEnd + 1);

      currentX += drawPartialText(
        windowText, currentX, adjustedY, currentFont,
        canvas, rawPaint);
      windowStart = windowEnd + 1;
    }
  }

  private float drawPartialText(
    String text, float x, float y, Font font,
    Canvas canvas, Paint rawPaint
  ) {
    short[] glyphs = font.getStringGlyphs(text);
    float[] glyphWidths = font.getWidths(glyphs);
    float[] glyphPositions = new float[glyphWidths.length];
    float distance = 0;
    for (int i = 0; i < glyphWidths.length; i++) {
      // TODO: Add letter spacing
      glyphPositions[i] = distance;
      distance += glyphWidths[i];
    }

    TextBlob textBlob = TextBlob.makeFromPosH(glyphs, glyphPositions, 0, font);
    canvas.drawTextBlob(textBlob, x, y, rawPaint);
    return distance;
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
      short glyph = rawFont.getUTF32Glyph(codePoint);
      if (glyph != 0) return rawFont;
    }

    // TODO: Scan all system fonts
    return rawFonts[0];
  }

}
