package net.buildabrowser.babbrowser.renderer.hintattr;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;

public class PresentationalHint implements IntrusiveList<PresentationalHint> {

  private final PresentationalHintName name;
  private final WeightedStyleRule rule;

  private PresentationalHint nextAttr;

  public PresentationalHint(
    PresentationalHintName name,
    WeightedStyleRule rule
  ) {
    this.name = name;
    this.rule = rule;
  }

  public PresentationalHintName name() {
    return this.name;
  }

  public WeightedStyleRule rule() {
    return this.rule;
  }

  @Override
  public PresentationalHint next() {
    return this.nextAttr;
  }

  @Override
  public void setNext(PresentationalHint nextAttr) {
    this.nextAttr = nextAttr;
  }

  public static enum PresentationalHintName {
    BGCOLOR, WIDTH, HEIGHT
  }

  public static PresentationalHintName lookup(String attrName) {
    return switch (attrName.toLowerCase()) {
      case "bgcolor" -> PresentationalHintName.BGCOLOR;
      case "width" -> PresentationalHintName.WIDTH;
      case "height" -> PresentationalHintName.HEIGHT;
      default -> null;
    };
  }

}
