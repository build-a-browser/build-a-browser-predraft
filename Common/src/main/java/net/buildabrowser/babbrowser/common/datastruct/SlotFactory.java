package net.buildabrowser.babbrowser.common.datastruct;

public interface SlotFactory<T extends Slottable, U extends SlotItem<U>> {
  
  U create(T slottable, short familyId);

}
