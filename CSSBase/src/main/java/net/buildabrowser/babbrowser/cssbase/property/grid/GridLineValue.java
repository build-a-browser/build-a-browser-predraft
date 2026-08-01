package net.buildabrowser.babbrowser.cssbase.property.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridLineValue(
  boolean isSpan,
  int lineNumber,
  CSSValue ident
) implements CSSValue {

  public static record CustomIdentValue(
    String value
  ) implements CSSValue {

    public static CustomIdentValue create(String value) {
      return new CustomIdentValue(value);
    }

  }

  public static record LineNumberValue(
    int lineNumber
  ) implements CSSValue {

    public static LineNumberValue create(int lineNumber) {
      return new LineNumberValue(lineNumber);
    }

  }

  public static GridLineValue create(
    boolean isSpan,
    int lineNumber,
    CSSValue ident
  ) {
    return new GridLineValue(isSpan, lineNumber, ident);
  }

}
