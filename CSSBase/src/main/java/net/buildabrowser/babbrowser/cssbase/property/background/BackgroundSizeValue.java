package net.buildabrowser.babbrowser.cssbase.property.background;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum BackgroundSizeValue implements CSSValue {
  
  COVER, CONTAIN;

  public static record SizedBackgroundSizeValue(
    CSSValue widthValue, CSSValue heightValue
  ) implements CSSValue {

    public static SizedBackgroundSizeValue create(
      CSSValue widthValue, CSSValue heightValue
    ) {
      return new SizedBackgroundSizeValue(widthValue, heightValue);
    }
    
  }

}
