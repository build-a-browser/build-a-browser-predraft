package net.buildabrowser.babbrowser.dom.imp;

import java.util.Iterator;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;

public class NodeListImp implements NodeList {

  private final Node parentNode;

  public NodeListImp(Node parentNode) {
    this.parentNode = parentNode;
  }

  @Override
  public long length() {
    int size = 0;
    Node current = parentNode.firstChild();
    while (current != null) {
      size++;
      current = current.nextSibling();
    }

    return size;
  }

  @Override
  public Iterator<Node> iterator() {
    return new Iterator<Node>() {
      private Node currentNode = parentNode.firstChild();

      @Override
      public boolean hasNext() {
        return currentNode != null;
      }

      @Override
      public Node next() {
        Node node = currentNode;
        this.currentNode = currentNode.nextSibling();
        return node;
      }
    };
  }

  @Override
  public Node item(long index) {
    Node current = parentNode.firstChild();
    while (index > 0) {
      index--;
      current = current.nextSibling();
    }

    return current;
  }
  
}
