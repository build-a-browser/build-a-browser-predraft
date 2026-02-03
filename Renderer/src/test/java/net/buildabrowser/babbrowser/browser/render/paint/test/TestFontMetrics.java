package net.buildabrowser.babbrowser.browser.render.paint.test;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;

public record TestFontMetrics(float fontHeight, float charWidth) implements FontMetrics {

  // Test with height 10 and width 5 per char
  public static TestFontMetrics create() {
    return new TestFontMetrics(10, 5);
  }

  public static TestFontMetrics create(float fontHeight, float charWidth) {
    return new TestFontMetrics(fontHeight, charWidth);
  }

  @Override
  public float stringWidth(String text) {
    return text.length() * charWidth;
  }

}
