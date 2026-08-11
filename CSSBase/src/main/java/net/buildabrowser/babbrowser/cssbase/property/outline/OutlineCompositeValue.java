package net.buildabrowser.babbrowser.cssbase.property.outline;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record OutlineCompositeValue(
  CSSValue outlineWidth,
  CSSValue outlineStyle,
  CSSValue outlineColor
) implements CSSValue {
  
  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(
      outlineWidth, outlineStyle, outlineColor);
  }

}
