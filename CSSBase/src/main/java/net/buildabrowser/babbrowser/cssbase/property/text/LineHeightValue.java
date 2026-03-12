package net.buildabrowser.babbrowser.cssbase.property.text;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum LineHeightValue implements CSSValue {
  
  NORMAL;

  public static record NumberHeight(Number multiplier) implements CSSValue {

    public static NumberHeight create(Number multiplier) {
      return new NumberHeight(multiplier);
    }

  }

}
