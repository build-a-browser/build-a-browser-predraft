package net.buildabrowser.babbrowser.cssbase.property.size;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record LengthValue(Number value, boolean integer, LengthType dimension) implements CSSValue {

  public static LengthValue ZERO = create(0, true, null);
  public static LengthValue THIN = LengthValue.create(2, true, LengthType.PX);
  public static LengthValue MEDIUM = LengthValue.create(4, true, LengthType.PX);
  public static LengthValue THICK = LengthValue.create(6, true, LengthType.PX);

  @Override
  public String serialize() {
    String unit = dimension == null ? "" : dimension.name().toLowerCase();
    return CSSSerializerUtil.serialize(value) + unit;
  }

  public static LengthValue create(Number value, boolean integer, LengthType dimension) {
    return new LengthValue(value, integer, dimension);
  }

  public static LengthValue create(int value, LengthType dimension) {
    return new LengthValue(value, true, dimension);
  }

  public static enum LengthType {
    EM, REM, EX, CH,
    CM, MM, IN, Q, PT, PC, PX,
    VW, VH, VMIN, VMAX,

    FR // Extension for Grid
  }

}
