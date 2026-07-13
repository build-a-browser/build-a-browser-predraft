package net.buildabrowser.babbrowser.painter.java2d;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;

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
  public float descent() {
    return innerFontMetrics.getDescent();
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
