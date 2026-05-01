package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FontWeightValue(int weight) implements CSSValue {
  
  public static FontWeightValue create(int weight) {
    return new FontWeightValue(weight);
  }

  public static enum RelativeFontWeightValue implements CSSValue {
    BOLDER, LIGHTER;
  }

}
