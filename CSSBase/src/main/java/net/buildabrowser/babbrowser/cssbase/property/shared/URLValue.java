package net.buildabrowser.babbrowser.cssbase.property.shared;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record URLValue(String value) implements CSSValue {
  
  public static URLValue create(String value) {
    return new URLValue(value);
  }

}
