package net.buildabrowser.babbrowser.cssbase.selector;

public enum SimplePseudoSelector implements SelectorPart {
  
  ROOT, HOVER, LINK, FOCUS, FOCUS_VISIBLE, FOCUS_WITHIN;

  public static SimplePseudoSelector lookupType(String name) {
    return switch (name) {
      case "root" -> SimplePseudoSelector.ROOT;
      case "hover" -> SimplePseudoSelector.HOVER;
      case "link" -> SimplePseudoSelector.LINK;
      case "focus" -> SimplePseudoSelector.FOCUS;
      case "focus-visible" -> SimplePseudoSelector.FOCUS_VISIBLE;
      case "focus-within" -> SimplePseudoSelector.FOCUS_WITHIN;
      default -> null;
    };
  }

}
