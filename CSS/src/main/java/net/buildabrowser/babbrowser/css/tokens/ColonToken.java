package net.buildabrowser.babbrowser.css.tokens;

public record ColonToken() implements Token {
  
  private static ColonToken INSTANCE = new ColonToken();

  public static ColonToken create() {
    return INSTANCE;
  }

}
