package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.box.Box;

public abstract class AbstractBoxImp implements Box {

  private Box nextBox;

  @Override
  public Box next() {
    return this.nextBox;
  }

  @Override
  public void setNext(Box nextNode) {
    this.nextBox = nextNode;
    assert IntrusiveList._ensureNoLoops(this);
  }
  
}
