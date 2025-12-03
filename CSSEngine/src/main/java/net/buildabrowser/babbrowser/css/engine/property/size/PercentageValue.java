package net.buildabrowser.babbrowser.css.engine.property.size;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record PercentageValue(int value) implements CSSValue {

  public static PercentageValue create(Number value) {
    return new PercentageValue(value.intValue());
  }

}
