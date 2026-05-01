package net.buildabrowser.babbrowser.cssbase.tokens;

public record LSBracketToken() implements Token {
  
  private static LSBracketToken INSTANCE = new LSBracketToken();

  public static LSBracketToken create() {
    return INSTANCE;
  }

}
