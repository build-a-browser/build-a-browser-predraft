package net.buildabrowser.babbrowser.render.paint.backend.skija;

import io.github.humbleui.skija.Font;
import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader.FontOptions;

public class SkijaFontMetrics implements FontMetrics {

  private final Font[] rawFonts;
  private final io.github.humbleui.skija.FontMetrics primaryMetrics;
  private final FontOptions fontOptions;

  private final float[] widthCache = new float[256];


  public SkijaFontMetrics(Font[] rawFonts, FontOptions fontOptions) {
    this.rawFonts = rawFonts;
    this.primaryMetrics = rawFonts[0].getMetrics();
    this.fontOptions = fontOptions;
  }

  @Override
  public float size() {
    return fontOptions.size();
  }

  @Override
  public int weight() {
    return fontOptions.weight();
  }

  @Override
  public float stringWidth(String text) {
    float textWidth = 0;
    for (int i = 0; i < text.length(); i++) {
      textWidth += getCharacterWidth(text.codePointAt(i));
    }

    return textWidth;
  }

  @Override
  public float height() {
    return primaryMetrics.getHeight();
  }

  @Override
  public float xHeight() {
    return primaryMetrics.getXHeight();
  }

  @Override
  public float ascent() {
    return primaryMetrics.getAscent();
  }

  private float getCharacterWidth(int codePoint) {
    if (codePoint < 256 && widthCache[codePoint] != 0) {
      return widthCache[codePoint];
    }
    
    short glyph = rawFonts[0].getUTF32Glyph(codePoint);
    float width = rawFonts[0].getWidths(new short[] { glyph })[0];
    for (int i = 1; i < rawFonts.length && glyph == 0; i++) {
      Font rawFont = rawFonts[i];
      glyph = rawFont.getUTF32Glyph(codePoint);
      if (glyph == 0) continue;
      width = rawFont.getWidths(new short[] { glyph })[0];
    }

    if (codePoint < 256) {
      widthCache[codePoint] = width;
    }
    
    return width;
  }

}
