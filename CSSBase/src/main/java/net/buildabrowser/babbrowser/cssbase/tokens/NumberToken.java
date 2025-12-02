package net.buildabrowser.babbrowser.cssbase.tokens;

public record NumberToken(Number value, boolean isInteger) implements Token {

  public static NumberToken create(Number value, boolean isInteger) {
    return isInteger ?
      new NumberToken(value.intValue(), isInteger) :
      new NumberToken(value, isInteger);
  }
  
  public static NumberToken create(int value) {
    return new NumberToken(value, true);
  }

}
