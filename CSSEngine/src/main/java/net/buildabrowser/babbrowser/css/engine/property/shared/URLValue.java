package net.buildabrowser.babbrowser.css.engine.property.shared;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record URLValue(String value) implements CSSValue {
  
  public static URLValue create(String value) {
    return new URLValue(value);
  }

}
