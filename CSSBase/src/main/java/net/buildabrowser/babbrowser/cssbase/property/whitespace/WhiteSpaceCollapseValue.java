package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum WhiteSpaceCollapseValue implements CSSValue {
  
  COLLAPSE, DISCARD, PRESERVE, PRESERVE_BREAKS, PRESERVE_SPACES, BREAK_SPACES;

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
