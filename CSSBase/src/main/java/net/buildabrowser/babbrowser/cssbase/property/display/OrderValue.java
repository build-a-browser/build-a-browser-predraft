package net.buildabrowser.babbrowser.cssbase.property.display;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record OrderValue(int order) implements CSSValue {
  
  public static OrderValue create(int order) {
    return new OrderValue(order);
  }

}
