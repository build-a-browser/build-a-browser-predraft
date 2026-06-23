package net.buildabrowser.babbrowser.painter.skija;

import java.util.List;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.skija.shaper.Shaper;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;

public class SkijaLoadedFont implements LoadedFont {

  private final Shaper shaper = Shaper.makeShapeDontWrapOrReorder();
  private final Font[] rawFonts;
  private final FontMetrics metrics;

  // TODO: Optimize for fonts outside of the ASCII range
  private final Font[] effectiveFontCache = new Font[256];

  public SkijaLoadedFont(Font[] rawFonts, FontOptions options) {
    this.rawFonts = rawFonts;
    this.metrics =  rawFonts.length > 0 ?
      new SkijaFontMetrics(rawFonts, this, options) :
      new SkijaNoOpFontMetrics();
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
    if (rawFonts.length == 0) {
      throw new IllegalStateException("Attempt to draw or measure text, but no font was set!");
    }
    if (text.isEmpty()) return 0;
    
    int windowStart = 0;
    float currentX = x;
    float adjustedY = y;// - metrics.ascent();
    while (windowStart < text.length()) {
      Font currentFont = glyphFont(text.codePointAt(windowStart));
      int windowEnd = endOfConsecutiveFontChars(text, windowStart);
      String windowText = text.substring(windowStart, windowEnd + 1);

      TextBlob line = shaper.shape(windowText, currentFont);
      linePosConsumer.accept(line, currentX, adjustedY);
      currentX += line.getBlockBounds().getWidth();
      windowStart = windowEnd + 1;
    }

    return currentX;
  }

  private int endOfConsecutiveFontChars(String text, int windowStart) {
    int firstCodepoint = text.codePointAt(windowStart);
    Font initialFont = glyphFont(text.codePointAt(windowStart));
    int windowEnd = windowStart + Character.charCount(firstCodepoint);
    while (windowEnd < text.length()) {
      Font font = glyphFont(text.codePointAt(windowEnd));
      if (font != initialFont) {
        return windowEnd - 1;
      }
      int cp = text.codePointAt(windowEnd);
      windowEnd += Character.charCount(cp);
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

    return rawFonts[0];
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
