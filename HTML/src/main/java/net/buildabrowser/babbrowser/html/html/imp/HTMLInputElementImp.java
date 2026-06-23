package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;

public class HTMLInputElementImp extends HTMLElementImp implements HTMLInputElement {

  private String value = "";

  public HTMLInputElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  // TODO: Limit to known attributes
  // TODO: Invalidate when type attribute changes
  @Override
  public String type() {
    return getAttribute("type");
  }

  @Override
  public void setType(String type) {
    addAttribute("type", type);
  }

  @Override
  public String value() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    this.value = value == null ? "" : value;
    // TODO: There might be cases where it should invalidate layout
    // instead - should delegate to the renderer
    invalidate(InvalidationLevel.PAINT);
  }
  
}
