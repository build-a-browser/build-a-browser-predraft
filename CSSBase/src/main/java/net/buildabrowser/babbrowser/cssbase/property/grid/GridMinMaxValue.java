package net.buildabrowser.babbrowser.cssbase.property.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridMinMaxValue(
  CSSValue min,
  CSSValue max
) implements CSSValue {
  
  public static GridMinMaxValue create(
    CSSValue min,
    CSSValue max
  ) {
    return new GridMinMaxValue(min, max);
  }

  @Override
  public String serialize() {
    return "<UNIMPLEMENTED>";
  }

}
