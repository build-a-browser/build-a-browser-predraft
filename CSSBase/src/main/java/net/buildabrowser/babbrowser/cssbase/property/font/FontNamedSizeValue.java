package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum FontNamedSizeValue implements CSSValue {

  XX_SMALL(3f/5f), X_SMALL(3f/4f), SMALL(8F/9F), MEDIUM(1),
  LARGE(6f/5f), X_LARGE(3f/2f), XX_LARGE(2f/1f), XXX_LARGE(3f/1f),
  LARGER(false, 1.35f), SMALLER(false, 1f/1.35f);

  public static float SCALING_FACTOR = 1.35f;

  private final boolean isAbsolute;
  private final float scaling;

  private FontNamedSizeValue(boolean isAbsolute, float scaling) {
    this.isAbsolute = isAbsolute;
    this.scaling = scaling;
  }

  private FontNamedSizeValue(float scaling) {
    this(true, scaling);
  }

  public boolean isAbsolute() {
    return this.isAbsolute;
  }

  public float scaling() {
    return this.scaling;
  }

}
