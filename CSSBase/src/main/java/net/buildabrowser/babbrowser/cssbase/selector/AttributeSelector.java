package net.buildabrowser.babbrowser.cssbase.selector;

public record AttributeSelector(String attrName, String attrValue, AttributeType type) implements SelectorPart {
  
  public static enum AttributeType {
    ONE_OF
  }

  public static AttributeSelector create(String attrName, String attrValue, AttributeType type) {
    return new AttributeSelector(attrName, attrValue, type);
  }

}
