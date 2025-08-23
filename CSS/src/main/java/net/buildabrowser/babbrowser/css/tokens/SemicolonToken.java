package net.buildabrowser.babbrowser.css.tokens;

public record SemicolonToken() implements Token {
  
  private static SemicolonToken INSTANCE = new SemicolonToken();

  public static SemicolonToken create() {
    return INSTANCE;
  }

}
