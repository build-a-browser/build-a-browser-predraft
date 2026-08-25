package net.buildabrowser.babbrowser.cssbase.property.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridLineValue(
  boolean isSpan,
  boolean allowAreaName,
  int lineNumber,
  String areaOrLineName
) implements CSSValue {

  public static record CustomIdentValue(
    String value
  ) implements CSSValue {

    public static CustomIdentValue create(String value) {
      return new CustomIdentValue(value);
    }

    @Override
    public String serialize() {
      return "<UNIMPLEMENTED>";
    }

  }

  public static record LineNumberValue(
    int lineNumber
  ) implements CSSValue {

    public static LineNumberValue create(int lineNumber) {
      return new LineNumberValue(lineNumber);
    }

    @Override
    public String serialize() {
      return "<UNIMPLEMENTED>";
    }

  }

  public static GridLineValue create(
    boolean isSpan,
    boolean allowAreaName,
    int lineNumber,
    String areaOrLineName
  ) {
    return new GridLineValue(isSpan, allowAreaName, lineNumber, areaOrLineName);
  }

  @Override
  public String serialize() {
    return "<UNIMPLEMENTED>";
  }

}
