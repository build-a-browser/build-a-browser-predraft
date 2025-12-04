package net.buildabrowser.babbrowser.cssbase.tokens;

public record CommaToken() implements Token {
  
  private static final CommaToken INSTANCE = new CommaToken();

  public static CommaToken create() {
    return INSTANCE;
  }

}
