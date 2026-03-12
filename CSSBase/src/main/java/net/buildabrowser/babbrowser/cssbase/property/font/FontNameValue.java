package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FontNameValue(String name) implements CSSValue {
 
  public static FontNameValue create(String name) {
    return new FontNameValue(name);
  }

}
