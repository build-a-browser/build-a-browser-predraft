package net.buildabrowser.babbrowser.browser.render.paint.skija;

import io.github.humbleui.skija.Font;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;

public class SkijaFontMetrics implements FontMetrics {

  private final Font rawFont;
  private final io.github.humbleui.skija.FontMetrics rawMetrics;

  private final float[] widthCache = new float[256];


  public SkijaFontMetrics(Font font) {
    this.rawFont = font;
    this.rawMetrics = font.getMetrics();
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
  public float fontHeight() {
    return rawMetrics.getHeight();
  }

  private float getCharacterWidth(int codePoint) {
    if (codePoint < 256 && widthCache[codePoint] != 0) {
      return widthCache[codePoint];
    }
    
    short glyph = rawFont.getUTF32Glyph(codePoint);
    float width = rawFont.getWidths(new short[] { glyph })[0];

    if (codePoint < 256) {
      widthCache[codePoint] = width;
    }
    
    return width;
  }

}
