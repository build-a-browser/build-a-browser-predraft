package net.buildabrowser.babbrowser.cssbase.property.align;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GapShorthandValue(CSSValue rowGap, CSSValue columnGap) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeMaybeEqual(rowGap, columnGap);
  }
  
}
