package net.buildabrowser.babbrowser.cssbase.selector;

public record DescendantCombinator() implements Combinator {
  
  private static DescendantCombinator INSTANCE = new DescendantCombinator();

  @Override
  public String serialize() {
    return " ";
  }

  public static DescendantCombinator create() {
    return INSTANCE;
  }

}
