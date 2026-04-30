package net.buildabrowser.babbrowser.cssbase.tokens;

public record BadURLToken() implements Token {
  
  private static final BadURLToken INSTANCE = new BadURLToken();

  public static BadURLToken create() {
    return INSTANCE;
  }

}
