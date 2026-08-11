package net.buildabrowser.babbrowser.cssbase.property.overflow;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record OverflowShorthandValue(
  CSSValue overflowX, CSSValue overflowY
) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeMaybeEqual(overflowX, overflowY);
  }

  public static CSSValue create(CSSValue xOverflow, CSSValue yOverflow) {
    return new OverflowShorthandValue(xOverflow, yOverflow);
  }

}