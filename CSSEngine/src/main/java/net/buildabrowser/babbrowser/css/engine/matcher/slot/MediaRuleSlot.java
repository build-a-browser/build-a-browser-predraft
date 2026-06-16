package net.buildabrowser.babbrowser.css.engine.matcher.slot;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;

public class MediaRuleSlot implements SlotItem<MediaRuleSlot> {

  private final short familyId;

  private boolean active;
  private int ruleSize = -1;
  private MediaRuleSlot next;

  public MediaRuleSlot(
    short familyId
  ) {
    this.familyId = familyId;
  }

  public boolean active() {
    return this.active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int ruleSize() {
    return this.ruleSize;
  }

  public void setRuleSize(int ruleSize) {
    this.ruleSize = ruleSize;
  }

  @Override
  public short familyId() {
    return this.familyId;
  }

  @Override
  public MediaRuleSlot next() {
    return this.next;
  }

  @Override
  public void setNext(MediaRuleSlot next) {
    this.next = next;
  }
  
}
