package net.buildabrowser.babbrowser.cssbase.property.shared;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record ManySideValue(CSSValue top, CSSValue right, CSSValue bottom, CSSValue left) implements CSSValue {
  
  @Override
  public String serialize() {
    if (
      top != null
      && top.equals(right)
      && top.equals(bottom)
      && top.equals(left)
    ) {
      return top.serialize();
    } else if (
      top != null
      && top.equals(bottom)
      && left != null
      && left.equals(right)
    ) {
      return CSSSerializerUtil.serializeManySpaces(top, left);
    } else {
      return CSSSerializerUtil.serializeManySpaces(top, right, bottom, left);
    }
  }
  
}
