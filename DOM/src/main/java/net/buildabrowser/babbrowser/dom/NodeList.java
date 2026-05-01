package net.buildabrowser.babbrowser.dom;

import net.buildabrowser.babbrowser.dom.imp.NodeListImp;

public interface NodeList extends Iterable<Node> {

  Node item(long index);

  long length();

  public static NodeList create(Node parentNode) {
    return new NodeListImp(parentNode);
  }

}
