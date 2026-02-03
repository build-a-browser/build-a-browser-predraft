package net.buildabrowser.babbrowser.dom;

public interface Node {
  
  Document ownerDocument();

  Node parentNode();

  NodeList childNodes();

  Node firstChild();

  Node lastChild();

  Node nextSibling();

  Node previousSibling();

  Node appendChild(Node node);

}
