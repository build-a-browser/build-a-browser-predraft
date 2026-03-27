package net.buildabrowser.babbrowser.render.box.imp;

import net.buildabrowser.babbrowser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.render.box.ElementBox;

public abstract class DocumentBoxImp extends AbstractBoxImp implements DocumentBox {

  private ElementBox childBox;

  @Override
  public ElementBox htmlBox() {
    return this.childBox;
  }

  @Override
  public void setChild(ElementBox child) {
    this.childBox = child;
  }
  
}
