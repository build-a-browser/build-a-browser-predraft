package net.buildabrowser.babbrowser.cssbase.tokens;

public record PercentageToken(Number value) implements Token {
  
  public static PercentageToken create(Number value) {
    return new PercentageToken(value.intValue());
  }

}
