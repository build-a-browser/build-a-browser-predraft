package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.datastruct.Slottable;
import net.buildabrowser.babbrowser.cssbase.media.ast.MediaNode;

public class MediaRule implements CSSRule, Slottable {

  private final MediaNode query;
  private final List<CSSRule> innerRules;
  
  
  private SlotItem<?> slots;

  public MediaRule(
    MediaNode query,
    List<CSSRule> innerRules
  ) {
    this.query = query;
    this.innerRules = innerRules;
  }

  public MediaNode query() {
    return this.query;
  }

  public List<CSSRule> innerRules() {
    return this.innerRules;
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
