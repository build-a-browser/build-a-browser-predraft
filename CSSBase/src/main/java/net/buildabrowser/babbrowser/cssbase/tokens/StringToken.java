package net.buildabrowser.babbrowser.cssbase.tokens;

public record StringToken(String value) implements Token {
  
  public static StringToken create(String value) {
    return new StringToken(value);
  }

}
