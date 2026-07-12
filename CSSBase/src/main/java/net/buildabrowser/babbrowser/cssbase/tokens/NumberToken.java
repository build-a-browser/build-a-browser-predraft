package net.buildabrowser.babbrowser.cssbase.tokens;

public record NumberToken(
  Number value, boolean isInteger, boolean isSigned
) implements Token {

  public static NumberToken create(
    Number value, boolean isInteger, boolean isSigned
  ) {
    return isInteger ?
      new NumberToken(value.intValue(), isInteger, isSigned) :
      new NumberToken(value, isInteger, isSigned);
  }

  public static NumberToken create(Number value, boolean isInteger) {
    return create(value, isInteger, false);
  }
  
  public static NumberToken create(int value) {
    return new NumberToken(value, true, false);
  }

}
