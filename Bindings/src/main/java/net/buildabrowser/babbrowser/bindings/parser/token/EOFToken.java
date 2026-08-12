package net.buildabrowser.babbrowser.bindings.parser.token;

public record EOFToken() implements IDLToken {
  
  private static final EOFToken INSTANCE = new EOFToken();

  public static EOFToken create() {
    return INSTANCE;
  }

}
