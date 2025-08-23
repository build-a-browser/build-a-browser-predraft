package net.buildabrowser.babbrowser.css.tokens;

public record EOFToken() implements Token {
  
  private static EOFToken INSTANCE = new EOFToken();

  public static EOFToken create() {
    return INSTANCE;
  }

}
