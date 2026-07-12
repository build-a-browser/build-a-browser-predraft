package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLInputElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLInputElement extends HTMLElement {

  String type();

  void setType(String type);
  
  String value();

  void setValue(String value);

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLInputElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
