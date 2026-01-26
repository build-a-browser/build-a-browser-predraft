package net.buildabrowser.babbrowser.css.engine.property.position;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record ZIndexValue(int zIndex) implements CSSValue {
  
  public static ZIndexValue create(int zIndex) {
    return new ZIndexValue(zIndex);
  }

}
