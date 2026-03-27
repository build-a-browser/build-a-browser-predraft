package net.buildabrowser.babbrowser.render.box;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public interface Box extends IntrusiveList<Box> {

  void invalidate(InvalidationLevel invalidationLevel);

  static enum InvalidationLevel {
    LAYOUT, PAINT
  }

}
