package net.buildabrowser.babbrowser.cssbase.property.border;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record BorderCompositeValue(CSSValue width, CSSValue color, CSSValue style) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(width, color, style);
  }
  
}
