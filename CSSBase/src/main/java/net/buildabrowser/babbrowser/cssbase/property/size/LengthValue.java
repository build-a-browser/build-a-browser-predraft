package net.buildabrowser.babbrowser.cssbase.property.size;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record LengthValue(Number value, boolean integer, LengthType dimension) implements CSSValue {

  public static LengthValue ZERO = create(0, true, null);
  public static LengthValue THIN = LengthValue.create(2, true, LengthType.PX);
  public static LengthValue MEDIUM = LengthValue.create(4, true, LengthType.PX);
  public static LengthValue THICK = LengthValue.create(6, true, LengthType.PX);

  public static LengthValue create(Number value, boolean integer, LengthType dimension) {
    return new LengthValue(value, integer, dimension);
  }

  public static enum LengthType {
    EM, REM, EX, CH,
    CM, MM, IN, Q, PT, PC, PX,
    VW, VH, VMIN, VMAX,

    FR // Extension for Grid
  }

}
