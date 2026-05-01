package net.buildabrowser.babbrowser.dom.imp;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;

public abstract class NodeImp implements Node {

  private final NodeList nodeList = NodeList.create(this);

  protected Node parentNode;
  private NodeImp firstChild;
  private NodeImp lastChild;
  private NodeImp previousSibling;
  private NodeImp nextSibling;

  @Override
  public Document nodeDocument() {
    return parentNode.nodeDocument();
  }

  @Override
  public Node parentNode() {
    return this.parentNode;
  }

  @Override
  public NodeList childNodes() {
    return nodeList;
  }

  @Override
  public Node firstChild() {
    return this.firstChild;
  }

  @Override
  public Node lastChild() {
    return this.lastChild;
  }

  @Override
  public Node previousSibling() {
    return this.previousSibling;
  }

  @Override
  public Node nextSibling() {
    return this.nextSibling;
  }

  @Override
  public Node appendChild(Node node) {
    // TODO: Use the spec method. Also, the cast is not so great
    NodeImp Node = (NodeImp) node;
    if (this.lastChild != null) {
      this.lastChild.nextSibling = Node;
    }
    Node.previousSibling = this.lastChild;
    this.lastChild = Node;
    if (this.firstChild == null) {
      this.firstChild = Node;
    }
    Node.parentNode = this;

    nodeDocument().changeListener().onNodeAdded(node); // Custom addition
    return node;
  }

}
