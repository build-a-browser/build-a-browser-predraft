package net.buildabrowser.babbrowser.painter.backend.skija;

import io.github.humbleui.skija.Font;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader.FontOptions;

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
    for (int i = 0; i < text.length();) {
      int codePoint = text.codePointAt(i);
      textWidth += getCharacterWidth(codePoint);
      i += Character.charCount(codePoint);
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
    for (Font rawFont: rawFonts) {
      glyph = rawFont.getUTF32Glyph(codePoint);
      if (glyph != 0) {
        width = rawFont.getWidths(new short[] { glyph })[0];
        break;
      }
    }

    if (codePoint < 256) {
      widthCache[codePoint] = width;
    }
    
    return width;
  }

}
