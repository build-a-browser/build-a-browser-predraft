package net.buildabrowser.babbrowser.cssbase.tokens;

public record RParenToken() implements Token {
  
  private static RParenToken INSTANCE = new RParenToken();

  public static RParenToken create() {
    return INSTANCE;
  }

}
