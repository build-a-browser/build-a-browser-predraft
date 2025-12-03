package net.buildabrowser.babbrowser.css.engine.property.size;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record LengthValue(Number value, boolean integer, LengthType dimension) implements CSSValue {

  public static CSSValue create(Number value, boolean integer, LengthType dimension) {
    return new LengthValue(value, integer, dimension);
  }

  public static enum LengthType {
    EM, EX, IN, CM, MM, PT, PC, PX
  }

}
