package net.buildabrowser.babbrowser.css.engine.matcher.slot;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;

public class ComplexSelectorSlot implements SlotItem<ComplexSelectorSlot> {

  private final ElementSet matchedElements;
  private final short familyId;

  private ComplexSelectorSlot next;

  public ComplexSelectorSlot(
    ElementSet matchedElements,
    short familyId
  ) {
    this.matchedElements = matchedElements;
    this.familyId = familyId;
  }

  public ElementSet matchedElements() {
    return this.matchedElements;
  }

  @Override
  public short familyId() {
    return this.familyId;
  }

  @Override
  public ComplexSelectorSlot next() {
    return this.next;
  }

  @Override
  public void setNext(ComplexSelectorSlot next) {
    this.next = next;
  }
  
}
