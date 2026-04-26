package net.buildabrowser.babbrowser.render.paint.backend.java2d;

import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader.FontOptions;

public class J2DFontMetrics implements FontMetrics {

  private final java.awt.FontMetrics innerFontMetrics;
  private final FontOptions fontOptions;

  public J2DFontMetrics(java.awt.FontMetrics innerFontMetrics, FontOptions fontOptions) {
    this.innerFontMetrics = innerFontMetrics;
    this.fontOptions = fontOptions;
  }

  // TODO: More accurate J2D stringWidth to consider fallbacks
  @Override
  public float stringWidth(String text) {
    return innerFontMetrics.stringWidth(text);
  }

  @Override
  public float height() {
    return innerFontMetrics.getHeight();
  }

  @Override
  public float xHeight() {
    return innerFontMetrics.getHeight() / 2;
  }

  @Override
  public float ascent() {
    return -innerFontMetrics.getAscent();
  }

  @Override
  public float size() {
    return fontOptions.size();
  }

  @Override
  public int weight() {
    return fontOptions.weight();
  }
  
}
