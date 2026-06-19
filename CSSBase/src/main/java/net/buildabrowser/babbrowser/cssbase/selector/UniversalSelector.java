package net.buildabrowser.babbrowser.cssbase.selector;

public record UniversalSelector() implements SimpleSelector {
  
  private static UniversalSelector INSTANCE = new UniversalSelector();

  public static UniversalSelector create() {
    return INSTANCE;
  }

}
