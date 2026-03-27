package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.imp.ElementImp;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.LinkElement;

public class LinkElementImp extends ElementImp implements LinkElement {

  public LinkElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  private CSSStyleSheet sheet;

  @Override
  public CSSStyleSheet sheet() {
    return this.sheet;
  }

  @Override
  public void setSheet(CSSStyleSheet sheet) {
    this.sheet = sheet;
  }
  
}
