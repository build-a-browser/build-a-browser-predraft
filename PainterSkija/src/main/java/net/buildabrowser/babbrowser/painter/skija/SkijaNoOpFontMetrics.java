package net.buildabrowser.babbrowser.painter.skija;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;

public class SkijaNoOpFontMetrics implements FontMetrics {

  private final String NO_OP_TEXT = "Cannot call methods on no-op font metrics!";

  @Override
  public float size() {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }

  @Override
  public int weight() {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }

  @Override
  public float stringWidth(String text) {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }

  @Override
  public float height() {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }

  @Override
  public float xHeight() {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }

  @Override
  public float ascent() {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }

  @Override
  public float descent() {
    throw new UnsupportedOperationException(NO_OP_TEXT);
  }
  
}
