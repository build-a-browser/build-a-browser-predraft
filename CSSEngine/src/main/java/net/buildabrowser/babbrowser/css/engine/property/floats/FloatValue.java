package net.buildabrowser.babbrowser.css.engine.property.floats;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record FloatValue(FloatSide side) implements CSSValue {
  
  public static enum FloatSide {
    NONE, LEFT, RIGHT;

    public boolean isSpecial(SpecialCSSType type) {
      return type.equals(SpecialCSSType.NONE) && this == NONE;
    }
  }

}
