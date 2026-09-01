package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLObjectElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLObjectElement extends HTMLElement {

  // Extensions

  ObjectRepresentation representation();

  void setRepresentation(ObjectRepresentation representation);

  //
 
  static HTMLObjectElement create(
    String name, Node parentNode
  ) {
    return new HTMLObjectElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

  interface ObjectRepresentation {}

  record ChildrenRepresentation() implements ObjectRepresentation {}

}
