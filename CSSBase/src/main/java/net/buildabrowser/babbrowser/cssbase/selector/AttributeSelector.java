package net.buildabrowser.babbrowser.cssbase.selector;

public record AttributeSelector(
  String attrName, String attrValue, AttributeType type
) implements SimpleSelector {
  
  @Override
  public String serialize() {
    if (
      type.equals(AttributeType.ONE_OF)
      && attrName.equals("class")
    ) {
      return '.' + attrValue;
    }

    String tail = switch (type) {
      case HAS_ATTR -> "";
      case EXACTLY -> "=" + attrValue;
      case ONE_OF -> "~=" + attrValue;
      case PREFIX -> "|=" + attrValue;
      case STARTS_WITH -> "^=" + attrValue;
      case ENDS_WITH -> "$=" + attrValue;
      case CONTAINS -> "*=" + attrValue;
      default -> throw new UnsupportedOperationException(
        "Unrecognized selector type: " + type);
    };

    return String.format("[%s%s]", attrName, tail);
  }

  public static enum AttributeType {
    HAS_ATTR, EXACTLY, ONE_OF, PREFIX, STARTS_WITH, ENDS_WITH, CONTAINS;
  }

  public static AttributeSelector create(String attrName, String attrValue, AttributeType type) {
    return new AttributeSelector(attrName, attrValue, type);
  }

}
