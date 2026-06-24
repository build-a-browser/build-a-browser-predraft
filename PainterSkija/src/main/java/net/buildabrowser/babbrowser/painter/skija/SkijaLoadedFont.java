package net.buildabrowser.babbrowser.painter.skija;

import java.util.ArrayList;
import java.util.List;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.skija.Typeface;
import io.github.humbleui.skija.shaper.Shaper;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;

public class SkijaLoadedFont implements LoadedFont {

  private final Shaper shaper = Shaper.makeShapeDontWrapOrReorder();
  private final List<SkijaFontEntry> rawFonts;
  private final FontMetrics metrics;

  // TODO: Optimize for fonts outside of the ASCII range
  private final SkijaFontEntry[] effectiveFontCache = new SkijaFontEntry[256];

  public SkijaLoadedFont(Font[] rawFonts, FontOptions options) {
    this.rawFonts = new ArrayList<>(rawFonts.length);
    this.metrics = rawFonts.length > 0 ?
      new SkijaFontMetrics(rawFonts, this, options) :
      new SkijaNoOpFontMetrics();

    for (Font rawFont: rawFonts) {
      this.rawFonts.add(new SkijaFontEntry(rawFont));
    }
  }

  @Override
  public FontMetrics metrics() {
    return this.metrics;
  }

  public void drawText(
    float x, float y, String text,
    Canvas canvas, Paint rawPaint
  ) {
    splitText(
      shaper,
      x, y, text,
      (line, x2, y2) -> canvas.drawTextBlob(line, x2, y2, rawPaint));
  }

  public float splitText(
    Shaper shaper,
    float x, float y, String text,
    TextLinePosConsumer linePosConsumer
  ) {
    if (rawFonts.isEmpty()) {
      throw new IllegalStateException("Attempt to draw or measure text, but no font was set!");
    }
    if (text.isEmpty()) return 0;
    
    int windowStart = 0;
    float currentX = x;
    float adjustedY = y - metrics.ascent();
    float[] runSize = new float[1];
    while (windowStart < text.length()) {
      SkijaFontEntry currentFont = glyphFont(text.codePointAt(windowStart));
      int windowEnd = endOfConsecutiveFontChars(text, windowStart);
      String windowText = text.substring(windowStart, windowEnd + 1);

      TextBlob line = SkijaHarfBuzzShaper.shape(windowText, currentFont, runSize);
      linePosConsumer.accept(line, currentX, adjustedY);
      currentX += runSize[0];
      windowStart = windowEnd + 1;
    }

    return currentX;
  }

  private int endOfConsecutiveFontChars(String text, int windowStart) {
    int firstCodepoint = text.codePointAt(windowStart);
    SkijaFontEntry initialFont = glyphFont(text.codePointAt(windowStart));
    int windowEnd = windowStart + Character.charCount(firstCodepoint);
    while (windowEnd < text.length()) {
      SkijaFontEntry font = glyphFont(text.codePointAt(windowEnd));
      if (font != initialFont) {
        return windowEnd - 1;
      }
      int cp = text.codePointAt(windowEnd);
      windowEnd += Character.charCount(cp);
    }

    return windowEnd - 1;
  }

  private SkijaFontEntry glyphFont(int codePoint) {
    // TODO: Look into caching codepoints outside of this, for better i18n performance
    if (
      codePoint < 256
      && effectiveFontCache[codePoint] != null
    ) {
      return effectiveFontCache[codePoint];
    }

    SkijaFontEntry rawFont = glyphFontRaw(codePoint);
    if (codePoint < 256) {
      effectiveFontCache[codePoint] = rawFont;
    }

    return rawFont;
  }

  private SkijaFontEntry glyphFontRaw(int codePoint) {
    for (SkijaFontEntry fontEntry: rawFonts) {
      short glyph = fontEntry.font().getUTF32Glyph(codePoint);
      if (glyph != 0) return fontEntry;
    }

    SkijaFontEntry fallbackFont = findFallbackFont(codePoint);
    if (fallbackFont != null) return fallbackFont;

    return rawFonts.get(0);
  }

  private SkijaFontEntry findFallbackFont(int codePoint) {
    Typeface primaryTypeface = rawFonts.get(0).font().getTypeface();
    Typeface fallback = FontMgr.getDefault().matchFamilyStyleCharacter(
      primaryTypeface.getFamilyName(),
      primaryTypeface.getFontStyle(),
      null, // TODO: Use lang attribute
      codePoint
    );

    if (fallback == null) return null;
    Font fallbackFont = new Font(fallback, metrics.size());
    SkijaFontEntry fallbackEntry = new SkijaFontEntry(fallbackFont);
    rawFonts.add(fallbackEntry);
    return fallbackEntry;
  }

  public static SkijaLoadedFont noFont() {
    return new SkijaLoadedFont(
      new Font[0],
      new FontOptions(List.of(), 12, 500));
  }

  public static interface TextLinePosConsumer {

    void accept(TextBlob line, float x, float y);

  }

}
