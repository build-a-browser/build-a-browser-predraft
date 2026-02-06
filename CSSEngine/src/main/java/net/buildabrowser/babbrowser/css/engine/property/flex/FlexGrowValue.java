package net.buildabrowser.babbrowser.css.engine.property.flex;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record FlexGrowValue(Number value) implements CSSValue {
 
  public static FlexGrowValue create(Number value) {
    return new FlexGrowValue(value);
  }

}
