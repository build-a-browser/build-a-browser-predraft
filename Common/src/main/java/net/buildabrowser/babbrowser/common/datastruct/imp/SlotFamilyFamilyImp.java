package net.buildabrowser.babbrowser.common.datastruct.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotFactory;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.datastruct.Slottable;

public class SlotFamilyFamilyImp implements SlotFamilyFamily {

  private short nextId = Short.MIN_VALUE;

  // TODO: Release unused IDs
  public <T extends Slottable, U extends SlotItem<U>> SlotFamily<T, U> createSlotFamily(
    SlotFactory<T, U> slotFactory
  ) {
    if (nextId == Short.MAX_VALUE) {
      throw new IllegalStateException("Out of slot family IDs to assign!");
    }

    return new SlotFamilyImp<>(nextId++, slotFactory);
  }
  
}
