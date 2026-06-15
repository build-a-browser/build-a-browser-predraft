package net.buildabrowser.babbrowser.common.datastruct;

public interface SlotItem<T extends SlotItem<T>> extends IntrusiveList<T> {
  
  short familyId();

  @SuppressWarnings("unchecked")
  static <T extends Slottable, U extends SlotItem<U>> U getExistingById(T slottable, short familyId) {
    U currentItem = (U) slottable.slots();
    while (currentItem != null) {
      if (currentItem.familyId() == familyId) {
        return (U) currentItem;
      }
      currentItem = currentItem.next();
    }

    return null;
  }

}
