package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLTextAreaElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLTextAreaElement extends FormAssociatedElement {
  
  boolean disabled();
  
  String value();

  void setValue(String value);
  
  public static HTMLTextAreaElement create(
    String name, Node parentNode
  ) {
    return new HTMLTextAreaElementImp(
      name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
