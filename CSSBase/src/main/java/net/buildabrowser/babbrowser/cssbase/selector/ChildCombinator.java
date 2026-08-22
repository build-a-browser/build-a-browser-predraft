package net.buildabrowser.babbrowser.cssbase.selector;

public record ChildCombinator() implements Combinator {
  
  private static ChildCombinator INSTANCE = new ChildCombinator();

  @Override
  public String serialize() {
    return " > ";
  }

  public static ChildCombinator create() {
    return INSTANCE;
  }

}
