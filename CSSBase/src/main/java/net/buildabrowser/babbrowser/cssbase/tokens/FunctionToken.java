package net.buildabrowser.babbrowser.cssbase.tokens;

public record FunctionToken(String value) implements Token {

  public static FunctionToken create(String value) {
    return new FunctionToken(value);
  }

}
