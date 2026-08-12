package net.buildabrowser.babbrowser.bindings.parser.token;

public record TerminalToken(String value) implements IDLToken {
  
  public static TerminalToken create(String value) {
    return new TerminalToken(value.intern());
  }

}
