package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FlexShrinkValue(Number value) implements CSSValue {
 
  public static FlexShrinkValue create(Number value) {
    return new FlexShrinkValue(value);
  }

}
