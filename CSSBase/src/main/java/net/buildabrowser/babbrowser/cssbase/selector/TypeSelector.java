package net.buildabrowser.babbrowser.cssbase.selector;

// TODO: Qualified name
public record TypeSelector(String tagName) implements SimpleSelector {
  
  public static TypeSelector create(String tagName) {
    return new TypeSelector(tagName);
  }

}
