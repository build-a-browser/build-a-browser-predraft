package net.buildabrowser.babbrowser.renderer.context.resolver;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;

public class LegacyAttribute implements IntrusiveList<LegacyAttribute> {

  private final LegacyAttributeName name;
  private final WeightedStyleRule rule;

  private LegacyAttribute nextAttr;

  public LegacyAttribute(
    LegacyAttributeName name,
    WeightedStyleRule rule
  ) {
    this.name = name;
    this.rule = rule;
  }

  public LegacyAttributeName name() {
    return this.name;
  }

  public WeightedStyleRule rule() {
    return this.rule;
  }

  @Override
  public LegacyAttribute next() {
    return this.nextAttr;
  }

  @Override
  public void setNext(LegacyAttribute nextAttr) {
    this.nextAttr = nextAttr;
  }

  public static enum LegacyAttributeName {
    BGCOLOR
  }

  public static LegacyAttributeName lookup(String attrName) {
    return switch (attrName.toLowerCase()) {
      case "bgcolor" -> LegacyAttributeName.BGCOLOR;
      default -> null;
    };
  }

}
