package net.buildabrowser.babbrowser.css.selector;

// TODO: Qualified name
public record TypeSelector(String tagName) implements SelectorPart {
  
  public static TypeSelector create(String tagName) {
    return new TypeSelector(tagName);
  }

}
