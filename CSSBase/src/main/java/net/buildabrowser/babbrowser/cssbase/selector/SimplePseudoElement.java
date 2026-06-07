package net.buildabrowser.babbrowser.cssbase.selector;

public enum SimplePseudoElement implements SelectorPart {
  
  BEFORE, AFTER;

  public static SimplePseudoElement lookupType(String name) {
    return switch (name) {
      case "before" -> SimplePseudoElement.BEFORE;
      case "after" -> SimplePseudoElement.AFTER;
      default -> null;
    };
  }

}
