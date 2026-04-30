package net.buildabrowser.babbrowser.cssbase.tokens;

public record BadStringToken() implements Token {
  
  private static final BadStringToken INSTANCE = new BadStringToken();

  public static BadStringToken create() {
    return INSTANCE;
  }

}
