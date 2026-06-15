package net.buildabrowser.babbrowser.common.datastruct;

public interface Slottable {
  
  void setSlots(SlotItem<?> slotItem);

  SlotItem<?> slots();

}
