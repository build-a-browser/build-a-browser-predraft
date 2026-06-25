package net.buildabrowser.babbrowser.dom;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.dom.events.EventTarget;

public interface Node extends EventTarget {
  
  Document nodeDocument();

  Node parentNode();

  NodeList childNodes();

  Node firstChild();

  Node lastChild();

  Node nextSibling();

  Node previousSibling();

  Node insertBefore(Node node, Node child);

  Node appendChild(Node node);

  default EventTarget getTheParent() {
    return parentNode();
  }

  default void forEachChild(Consumer<Node> itFunc) {
    Node currentNode = firstChild();
    while (currentNode != null) {
      itFunc.accept(currentNode);
      currentNode = currentNode.nextSibling();
    }
  }

}
