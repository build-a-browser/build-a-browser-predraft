package net.buildabrowser.babbrowser.cssbase.selector;

public record NextSiblingCombinator() implements Combinator {
  
  private static NextSiblingCombinator INSTANCE = new NextSiblingCombinator();

  @Override
  public String serialize() {
    return " + ";
  }

  public static NextSiblingCombinator create() {
    return INSTANCE;
  }

}
