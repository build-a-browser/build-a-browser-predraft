package net.buildabrowser.babbrowser.cssbase.property.size;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum BoxSizingValue implements CSSValue {
  
  CONTENT_BOX, BORDER_BOX;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
