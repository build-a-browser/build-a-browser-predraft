package net.buildabrowser.babbrowser.cssbase.tokens;

public record DimensionToken(Number value, boolean isInteger, String dimension) implements Token {
  
  public static DimensionToken create(Number value, boolean isInteger, String dimension) {
    return isInteger ?
      new DimensionToken(value.intValue(), isInteger, dimension) :
      new DimensionToken(value, isInteger, dimension);
  }

  public static DimensionToken create(int value, String dimension) {
    return new DimensionToken(value, true, dimension);
  }

}
