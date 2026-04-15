package net.buildabrowser.babbrowser.cssbase.selector;

public enum SimplePsuedoSelector implements SelectorPart {
  
  ROOT;

  public static SimplePsuedoSelector lookupType(String name) {
    return switch (name) {
      case "root" -> SimplePsuedoSelector.ROOT;
      default -> null;
    };
  }

}
