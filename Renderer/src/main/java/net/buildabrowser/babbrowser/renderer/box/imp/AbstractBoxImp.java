package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.box.Box;

public abstract class AbstractBoxImp implements IntrusiveList<Box> {

  private Box nextBox;

  public Box next() {
    return this.nextBox;
  }

  public void setNext(Box nextNode) {
    this.nextBox = nextNode;
    assert IntrusiveList._ensureNoLoops(this);
  }
  
}
