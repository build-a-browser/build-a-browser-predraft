package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum FontFamilyValue implements CSSValue {
  
  // TODO: Also add others
  SERIF, SANS_SERIF, MONOSPACE;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
