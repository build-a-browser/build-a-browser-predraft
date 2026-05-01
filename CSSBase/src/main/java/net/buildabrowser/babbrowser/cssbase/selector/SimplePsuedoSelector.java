package net.buildabrowser.babbrowser.cssbase.selector;

public enum SimplePsuedoSelector implements SelectorPart {
  
  ROOT, HOVER;

  public static SimplePsuedoSelector lookupType(String name) {
    return switch (name) {
      case "root" -> SimplePsuedoSelector.ROOT;
      case "hover" -> SimplePsuedoSelector.HOVER;
      default -> null;
    };
  }

}
