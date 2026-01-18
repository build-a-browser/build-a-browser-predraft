package net.buildabrowser.babbrowser.cssbase.tokens;

public record LParenToken() implements Token {
  
  private static LParenToken INSTANCE = new LParenToken();

  public static LParenToken create() {
    return INSTANCE;
  }

}
