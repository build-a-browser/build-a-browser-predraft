package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.Box;

public abstract class AbstractBoxImp implements Box {

  private Box nextBox;

  @Override
  public Box next() {
    return this.nextBox;
  }

  @Override
  public void setNext(Box nextNode) {
    this.nextBox = nextNode;
  }
  
}
