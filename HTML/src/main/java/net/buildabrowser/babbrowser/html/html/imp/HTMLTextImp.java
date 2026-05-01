package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.dom.imp.TextImp;
import net.buildabrowser.babbrowser.html.html.HTMLText;

public class HTMLTextImp extends TextImp implements HTMLText {

  private Object box;

  public HTMLTextImp(String text) {
    super(text);
  }
  
  @Override
  public Object getBox() {
    return this.box;
  }

  @Override
  public void setBox(Object box) {
    this.box = box;
  }
  
}
