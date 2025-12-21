package net.buildabrowser.babbrowser.dom;

public interface Node {
  
  Document ownerDocument();

  Node parentNode();

  NodeList childNodes();

  Node appendChild(Node node);

}
