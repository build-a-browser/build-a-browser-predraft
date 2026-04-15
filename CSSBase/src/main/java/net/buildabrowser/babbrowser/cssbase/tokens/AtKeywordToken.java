package net.buildabrowser.babbrowser.cssbase.tokens;

public record AtKeywordToken(String value) implements Token {

  public static AtKeywordToken create(String value) {
    return new AtKeywordToken(value);
  }

}
