package net.buildabrowser.babbrowser.browser.render.paint.java2d;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;

public class J2DFontMetrics implements FontMetrics {

  private final java.awt.FontMetrics innerFontMetrics;

  public J2DFontMetrics(java.awt.FontMetrics innerFontMetrics) {
    this.innerFontMetrics = innerFontMetrics;
  }

  @Override
  public float stringWidth(String text) {
    return innerFontMetrics.stringWidth(text);
  }

  @Override
  public float fontHeight() {
    return innerFontMetrics.getHeight();
  }
  
}
