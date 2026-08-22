package net.buildabrowser.babbrowser.cssbase.selector;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;

public enum SimplePseudoElement implements SelectorPart {
  
  BEFORE, AFTER;

  @Override
  public String serialize() {
    return "::" + CSSSerializerUtil.serializeEnum(this);
  }

  public static SimplePseudoElement lookupType(String name) {
    return switch (name) {
      case "before" -> SimplePseudoElement.BEFORE;
      case "after" -> SimplePseudoElement.AFTER;
      default -> null;
    };
  }

}
