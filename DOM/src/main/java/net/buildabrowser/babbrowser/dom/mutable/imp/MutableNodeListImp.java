package net.buildabrowser.babbrowser.dom.mutable.imp;

import java.util.Iterator;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.dom.mutable.MutableNodeList;

public class MutableNodeListImp implements MutableNodeList {

  private final MutableNode parentNode;

  public MutableNodeListImp(MutableNode parentNode) {
    this.parentNode = parentNode;
  }

  @Override
  public long length() {
    int size = 0;
    MutableNode current = parentNode.firstChild();
    while (current != null) {
      size++;
      current = current.nextSibling();
    }

    return size;
  }

  @Override
  public Iterator<Node> iterator() {
    return new Iterator<Node>() {
      private MutableNode currentNode = parentNode.firstChild();

      @Override
      public boolean hasNext() {
        return currentNode != null;
      }

      @Override
      public Node next() {
        MutableNode node = currentNode;
        this.currentNode = currentNode.nextSibling();
        return node;
      }
    };
  }

  @Override
  public MutableNode item(long index) {
    MutableNode current = parentNode.firstChild();
    while (index > 0) {
      index--;
      current = current.nextSibling();
    }

    return current;
  }
  
}
