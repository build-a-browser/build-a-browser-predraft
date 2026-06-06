package net.buildabrowser.babbrowser.cssbase.selector;

public enum SimplePseudoSelector implements SelectorPart {
  
  ROOT, HOVER, LINK;

  public static SimplePseudoSelector lookupType(String name) {
    return switch (name) {
      case "root" -> SimplePseudoSelector.ROOT;
      case "hover" -> SimplePseudoSelector.HOVER;
      case "link" -> SimplePseudoSelector.LINK;
      default -> null;
    };
  }

}
