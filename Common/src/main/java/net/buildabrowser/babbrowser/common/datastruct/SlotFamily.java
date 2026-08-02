package net.buildabrowser.babbrowser.common.datastruct;

public interface SlotFamily<T extends Slottable, U extends SlotItem<U>> {
  
  U get(T slottable);

  short familyId();

}
