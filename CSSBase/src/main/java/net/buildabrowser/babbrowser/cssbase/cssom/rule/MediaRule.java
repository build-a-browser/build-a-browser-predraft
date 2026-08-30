package net.buildabrowser.babbrowser.cssbase.cssom.rule;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.datastruct.Slottable;
import net.buildabrowser.babbrowser.cssbase.media.ast.MediaNode;

public class MediaRule implements NestingRule, Slottable {

  private final MediaNode query;
  private final List<CSSRule> nestedRules;
  
  private SlotItem<?> slots;

  public MediaRule(
    MediaNode query,
    List<CSSRule> innerRules
  ) {
    this.query = query;
    this.nestedRules = innerRules;
  }

  public MediaNode query() {
    return this.query;
  }

  public List<CSSRule> nestedRules() {
    return this.nestedRules;
  }

  @Override
  public void setSlots(SlotItem<?> slots) {
    this.slots = slots;
  }

  @Override
  public SlotItem<?> slots() {
    return this.slots;
  }

}
