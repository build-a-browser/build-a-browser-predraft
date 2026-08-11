package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record WhiteSpaceCompositeValue(
  CSSValue whiteSpaceCollapse,
  CSSValue textWrapMode,
  CSSValue whiteSpaceTrim
) implements CSSValue {
  
  public static WhiteSpaceCompositeValue create(
    CSSValue whiteSpaceCollapse,
    CSSValue textWrapMode,
    CSSValue whiteSpaceTrim
  ) {
    return new WhiteSpaceCompositeValue(
      whiteSpaceCollapse, textWrapMode, whiteSpaceTrim);
  }

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(
      whiteSpaceCollapse, textWrapMode, whiteSpaceTrim);
  }

}
