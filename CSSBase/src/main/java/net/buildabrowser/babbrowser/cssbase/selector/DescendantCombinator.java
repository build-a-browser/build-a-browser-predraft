package net.buildabrowser.babbrowser.cssbase.selector;

public record DescendantCombinator() implements Combinator {
  
  private static DescendantCombinator INSTANCE = new DescendantCombinator();

  public static DescendantCombinator create() {
    return INSTANCE;
  }

}
