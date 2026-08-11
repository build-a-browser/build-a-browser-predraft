package net.buildabrowser.babbrowser.cssbase.property.size;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record PercentageValue(int value) implements CSSValue {
  
  @Override
  public String serialize() {
    return String.valueOf(value) + "%";
  }

  public static PercentageValue create(Number value) {
    return new PercentageValue(value.intValue());
  }

}
