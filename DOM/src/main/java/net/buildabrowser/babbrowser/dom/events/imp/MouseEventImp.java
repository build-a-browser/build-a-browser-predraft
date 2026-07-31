package net.buildabrowser.babbrowser.dom.events.imp;

import net.buildabrowser.babbrowser.dom.events.MouseEvent;
import net.buildabrowser.babbrowser.dom.events.util.ModifierUtil;

public class MouseEventImp implements MouseEvent {

  private final String type;
  private final byte modifiers;

  public MouseEventImp(String type, byte modifiers) {
    this.type = type;
    this.modifiers = modifiers;
  }

  @Override
  public String type() {
    return this.type;
  }

  public boolean altKey() {
    return (modifiers & ModifierUtil.MODIFIER_ALT) != 0;
  }

  public boolean ctrlKey() {
    return (modifiers & ModifierUtil.MODIFIER_CTRL) != 0;
  }

  public boolean metaKey() {
    return (modifiers & ModifierUtil.MODIFIER_META) != 0;
  }

  public boolean shiftKey() {
    return (modifiers & ModifierUtil.MODIFIER_SHIFT) != 0;
  }
  
}
