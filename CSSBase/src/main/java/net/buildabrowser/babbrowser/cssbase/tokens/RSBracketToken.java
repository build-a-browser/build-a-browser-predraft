package net.buildabrowser.babbrowser.cssbase.tokens;

public record RSBracketToken() implements Token {
  
  private static RSBracketToken INSTANCE = new RSBracketToken();

  public static RSBracketToken create() {
    return INSTANCE;
  }

}
