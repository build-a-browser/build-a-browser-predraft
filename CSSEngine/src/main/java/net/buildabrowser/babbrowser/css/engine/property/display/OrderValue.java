package net.buildabrowser.babbrowser.css.engine.property.display;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record OrderValue(int order) implements CSSValue {
  
  public static OrderValue create(int order) {
    return new OrderValue(order);
  }

}
