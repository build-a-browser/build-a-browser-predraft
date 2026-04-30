package net.buildabrowser.babbrowser.cssbase.tokens;

public record URLToken(String value) implements Token {
  
  public static URLToken create(String value) {
    return new URLToken(value);
  }

}
