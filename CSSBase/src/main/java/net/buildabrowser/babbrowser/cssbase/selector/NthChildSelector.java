package net.buildabrowser.babbrowser.cssbase.selector;

public record NthChildSelector(
  NthChildSelectorType type,
  int index
) {
  
  public static enum NthChildSelectorType {

  }

}
