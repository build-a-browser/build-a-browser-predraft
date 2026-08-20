package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum BackgroundAttachmentValue implements CSSValue {

  SCROLL, FIXED, LOCAL;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
