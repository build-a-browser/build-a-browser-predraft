package net.buildabrowser.babbrowser.dom.mutable.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.dom.mutable.MutableNodeList;

public abstract class MutableNodeImp implements MutableNode {

  private final List<MutableNode> childNodes = new LinkedList<>();
  private final MutableNodeList nodeList = MutableNodeList.create(childNodes);

  private final MutableNode parentNode;

  private Object context;

  public MutableNodeImp(MutableNode parentNode) {
    this.parentNode = parentNode;
  }

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
  public Node appendChild(Node node) {
    // TODO: Use the spec method. Also, the cast is not so great
    childNodes.add((MutableNode) node);
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
