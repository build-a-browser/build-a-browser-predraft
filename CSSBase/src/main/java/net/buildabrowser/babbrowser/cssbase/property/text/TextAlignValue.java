package net.buildabrowser.babbrowser.cssbase.property.text;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum TextAlignValue implements CSSValue {
  
  START, END, LEFT, RIGHT, CENTER, JUSTIFY, MATCH_PARENT, JUSTIFY_ALL,
  // UA-specific properties to align descendants
  _BAB_LEFT(true), _BAB_RIGHT(true), _BAB_CENTER(true), _BAB_JUSTIFY(true);

  private final boolean alignsDescendants;

  private TextAlignValue() {
    this.alignsDescendants = false;
  }

  private TextAlignValue(boolean alignsDescendants) {
    this.alignsDescendants = alignsDescendants;
  }

  public boolean alignsDescendants() {
    return this.alignsDescendants;
  }
  
  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeEnum(this);
  }

}
