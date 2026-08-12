package net.buildabrowser.babbrowser.bindings.parser.token;

public record OtherToken(String value) implements IDLToken {
  
  public static OtherToken create(String value) {
    return new OtherToken(value);
  }

}
