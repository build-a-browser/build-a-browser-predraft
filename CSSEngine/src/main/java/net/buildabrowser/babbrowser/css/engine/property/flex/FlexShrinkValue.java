package net.buildabrowser.babbrowser.css.engine.property.flex;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record FlexShrinkValue(Number value) implements CSSValue {
 
  public static FlexShrinkValue create(Number value) {
    return new FlexShrinkValue(value);
  }

}
