package net.buildabrowser.babbrowser.css.tokens;

public record IdentToken(String value) implements Token {

  public static IdentToken create(String value) {
    return new IdentToken(value);
  }

}
