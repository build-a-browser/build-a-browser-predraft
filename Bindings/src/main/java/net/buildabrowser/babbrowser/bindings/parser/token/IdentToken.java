package net.buildabrowser.babbrowser.bindings.parser.token;

public record IdentToken(String value) implements IDLToken {
  
  public static IdentToken create(String value) {
    return new IdentToken(value);
  }

}
