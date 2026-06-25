package net.buildabrowser.babbrowser.dom.imp;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;

public abstract class NodeImp implements Node {

  private NodeList nodeList;

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

  // Avoid iterating over this, prefer forEachChild or a manual loop
  @Override
  public NodeList childNodes() {
    if (nodeList == null) {
      this.nodeList = NodeList.create(this);
    }
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
  public Node insertBefore(Node node, Node child) {
    // TODO: Use the spec method
    if (child == null) {
      return appendChild(node);
    }

    if (child.parentNode() != this) {
      throw new IllegalStateException("Child not in tree!");
    }

    NodeImp mutNode = (NodeImp) node;
    NodeImp mutChild = (NodeImp) child;
    if (child.previousSibling() != null) {
      NodeImp mutPrev = (NodeImp) child.previousSibling();
      mutPrev.nextSibling = mutNode;
      mutNode.previousSibling = mutPrev;
    } else {
      this.firstChild = mutNode;
    }
    mutChild.previousSibling = mutNode;
    mutNode.nextSibling = mutChild;
    mutNode.parentNode = this;

    nodeDocument().changeListener().onNodeAdded(node); // Custom addition
    return node;
  }

  @Override
  public Node appendChild(Node node) {
    // TODO: Use the spec method. Also, the cast is not so great
    NodeImp mutNode = (NodeImp) node;
    if (this.lastChild != null) {
      this.lastChild.nextSibling = mutNode;
    }
    mutNode.previousSibling = this.lastChild;
    this.lastChild = mutNode;
    if (this.firstChild == null) {
      this.firstChild = mutNode;
    }
    mutNode.parentNode = this;

    nodeDocument().changeListener().onNodeAdded(node); // Custom addition
    return node;
  }

}
