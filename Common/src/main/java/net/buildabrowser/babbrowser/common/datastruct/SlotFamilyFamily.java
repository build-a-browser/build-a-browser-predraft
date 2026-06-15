package net.buildabrowser.babbrowser.common.datastruct;

import net.buildabrowser.babbrowser.common.datastruct.imp.SlotFamilyFamilyImp;

public interface SlotFamilyFamily {
  
  <T extends Slottable, U extends SlotItem<U>> SlotFamily<T, U> createSlotFamily(
    SlotFactory<T, U> slotFactory
  );

  static SlotFamilyFamily create() {
    return new SlotFamilyFamilyImp();
  }

}
