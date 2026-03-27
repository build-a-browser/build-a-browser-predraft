package net.buildabrowser.babbrowser.dom;

public interface Node {
  
  Document nodeDocument();

  Node parentNode();

  NodeList childNodes();

  Node firstChild();

  Node lastChild();

  Node nextSibling();

  Node previousSibling();

  Node appendChild(Node node);

  // Extensions

  // There's not type information accessible from here, so use Object

  Object getContext();

  void setContext(Object context);
  
  Object getBox();

  void setBox(Object box);

}
