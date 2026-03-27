package net.buildabrowser.babbrowser.render.box.imp;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.render.box.DocumentBox;
import net.buildabrowser.babbrowser.render.box.ElementBox;

public class DocumentBoxImp extends AbstractBoxImp implements DocumentBox {

  private final HTMLDocument document;

  private ElementBox childBox;

  public DocumentBoxImp(HTMLDocument document) {
    this.document = document;
  }

  @Override
  public HTMLDocument document() {
    return this.document;
  }

  @Override
  public ElementBox htmlBox() {
    return this.childBox;
  }

  @Override
  public void setChild(ElementBox child) {
    this.childBox = child;
  }
  
}
