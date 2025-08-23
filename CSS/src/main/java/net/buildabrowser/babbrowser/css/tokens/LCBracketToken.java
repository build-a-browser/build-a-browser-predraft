package net.buildabrowser.babbrowser.css.tokens;

public record LCBracketToken() implements Token {
  
  private static LCBracketToken INSTANCE = new LCBracketToken();

  public static LCBracketToken create() {
    return INSTANCE;
  }

}
