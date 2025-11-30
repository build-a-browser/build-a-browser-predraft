package net.buildabrowser.babbrowser.css.engine.property.display;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public final class DisplayValue {
  
  private DisplayValue() {}

  public static enum OuterDisplayValue implements CSSValue {
    BLOCK, INLINE, RUN_IN, CONTENTS, NONE
  }

  public static enum InnerDisplayValue implements CSSValue {
    FLOW, FLOW_ROOT, TABLE, FLEX, GRID, RUBY
  }

  public static record DisplayUnionValue(
    OuterDisplayValue outerDisplayValue, InnerDisplayValue innerDisplayValue
  ) implements CSSValue {};

}
