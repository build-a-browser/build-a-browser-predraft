package net.buildabrowser.babbrowser.common.datastruct.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotFactory;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.datastruct.Slottable;

public class SlotFamilyImp<T extends Slottable, U extends SlotItem<U>> implements SlotFamily<T, U> {

  private final short familyId;
  private final SlotFactory<T, U> slotFactory;

  public SlotFamilyImp(
    short familyId,
    SlotFactory<T, U> slotFactory
  ) {
    this.familyId = familyId;
    this.slotFactory = slotFactory;
  }

  // TODO: Allow for freeing the slots
  @Override
  @SuppressWarnings("unchecked")
  public U get(T slottable) {
    U slotItem = SlotItem.getExistingById(slottable, familyId);
    if (slotItem != null) return slotItem;

    slotItem = slotFactory.create(slottable, familyId);
    slotItem.setNext((U) slottable.slots());
    slottable.setSlots(slotItem);
    return slotItem;
  }

  @Override
  public short familyId() {
    return this.familyId;
  }
  
}
