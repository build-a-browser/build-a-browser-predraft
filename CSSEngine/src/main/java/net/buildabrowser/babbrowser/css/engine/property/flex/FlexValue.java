package net.buildabrowser.babbrowser.css.engine.property.flex;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record FlexValue(
  FlexGrowValue flexGrow, FlexShrinkValue flexShrink, CSSValue flexBasis
) implements CSSValue {
  
  public static FlexValue create(
    FlexGrowValue flexGrow, FlexShrinkValue flexShrink, CSSValue flexBasis
  ) {
    return new FlexValue(flexGrow, flexShrink, flexBasis);
  }

}
