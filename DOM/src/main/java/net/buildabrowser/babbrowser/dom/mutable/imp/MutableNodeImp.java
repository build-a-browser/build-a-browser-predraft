package net.buildabrowser.babbrowser.dom.mutable.imp;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.dom.mutable.MutableNodeList;

public abstract class MutableNodeImp implements MutableNode {

  private final MutableNodeList nodeList = MutableNodeList.create(this);

  protected MutableNode parentNode;
  private MutableNodeImp firstChild;
  private MutableNodeImp lastChild;
  private MutableNodeImp previousSibling;
  private MutableNodeImp nextSibling;

  private Object context;

  @Override
  public MutableDocument ownerDocument() {
    return parentNode.ownerDocument();
  }

  @Override
  public MutableNode parentNode() {
    return this.parentNode;
  }

  @Override
  public MutableNodeList childNodes() {
    return nodeList;
  }

  @Override
  public MutableNode firstChild() {
    return this.firstChild;
  }

  @Override
  public MutableNode lastChild() {
    return this.lastChild;
  }

  @Override
  public MutableNode previousSibling() {
    return this.previousSibling;
  }

  @Override
  public MutableNode nextSibling() {
    return this.nextSibling;
  }

  @Override
  public Node appendChild(Node node) {
    // TODO: Use the spec method. Also, the cast is not so great
    MutableNodeImp mutableNode = (MutableNodeImp) node;
    if (this.lastChild != null) {
      this.lastChild.nextSibling = mutableNode;
    }
    mutableNode.previousSibling = this.lastChild;
    this.lastChild = mutableNode;
    if (this.firstChild == null) {
      this.firstChild = mutableNode;
    }
    mutableNode.parentNode = this;

    ownerDocument().changeListener().onNodeAdded(node); // Custom addition
    return node;
  }

  @Override
  public void setContext(Object context) {
    this.context = context;
  }

  @Override
  public Object getContext() {
    return this.context;
  }
  
}
