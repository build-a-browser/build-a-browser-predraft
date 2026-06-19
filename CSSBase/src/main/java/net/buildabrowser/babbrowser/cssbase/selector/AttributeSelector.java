package net.buildabrowser.babbrowser.cssbase.selector;

public record AttributeSelector(
  String attrName, String attrValue, AttributeType type
) implements SimpleSelector {
  
  public static enum AttributeType {
    HAS_ATTR, EXACTLY, ONE_OF, PREFIX, STARTS_WITH, ENDS_WITH, CONTAINS;
  }

  public static AttributeSelector create(String attrName, String attrValue, AttributeType type) {
    return new AttributeSelector(attrName, attrValue, type);
  }

}
