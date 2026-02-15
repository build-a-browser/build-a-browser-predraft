package net.buildabrowser.babbrowser.cssbase.property.position;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record ZIndexValue(int zIndex) implements CSSValue {
  
  public static ZIndexValue create(int zIndex) {
    return new ZIndexValue(zIndex);
  }

}
