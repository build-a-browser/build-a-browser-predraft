package net.buildabrowser.babbrowser.css.engine.property.floats;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record ClearValue(ClearSide side) implements CSSValue {
  
  public static enum ClearSide {
    NONE, LEFT, RIGHT, BOTH;

    public boolean isSpecial(SpecialCSSType type) {
      return type.equals(SpecialCSSType.NONE) && this == NONE;
    }
  }

}
