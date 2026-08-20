package net.buildabrowser.babbrowser.cssbase.property.position;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record ZIndexValue(int zIndex) implements CSSValue {

  @Override
  public String serialize() {
    return String.valueOf(zIndex);
  }
  
  public static ZIndexValue create(int zIndex) {
    return new ZIndexValue(zIndex);
  }

}
