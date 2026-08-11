package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FontShorthandValue(
  CSSValue fontWeight, CSSValue fontSize, CSSValue lineHeight, CSSValue fontFamily
) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(
      fontWeight, fontSize, lineHeight, fontFamily);
  }

}
