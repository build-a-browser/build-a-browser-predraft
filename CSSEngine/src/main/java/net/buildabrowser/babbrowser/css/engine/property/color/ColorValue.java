package net.buildabrowser.babbrowser.css.engine.property.color;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public interface ColorValue extends CSSValue {
  
  int asSARGB();
  
  default int red() {
    return (asSARGB() >> 16) & 0xFF;
  }

  default int green() {
    return (asSARGB() >> 8) & 0xFF;
  }

  default int blue() {
    return asSARGB() & 0xFF;
  }

  default int alpha() {
    return (asSARGB() >> 24) & 0xFF;
  }

  static record SRGBAColor(int red, int green, int blue, int alpha) implements ColorValue {

    @Override
    public int asSARGB() {
      return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static SRGBAColor create(int r, int g, int b, int a) {
      return new SRGBAColor(r, g, b, a);
    }

  }

}
