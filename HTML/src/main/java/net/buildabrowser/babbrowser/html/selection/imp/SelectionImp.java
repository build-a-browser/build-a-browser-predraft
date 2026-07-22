package net.buildabrowser.babbrowser.html.selection.imp;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.html.selection.SelectionUtil;

public class SelectionImp implements Selection {

  private final Document nodeDocument;

  private Node anchorNode;
  private long anchorOffset;
  private Node focusNode;
  private long focusOffset;
  private SelectionDirection direction;

  public SelectionImp(Document nodeDocument) {
    this.nodeDocument = nodeDocument;
  }

  @Override
  public Node anchorNode() {
    if (!nodeValid(anchorNode)) return null;
    return this.anchorNode;
  }

  @Override
  public long anchorOffset() {
    if (!nodeValid(anchorNode)) return 0;
    return this.anchorOffset;
  }

  @Override
  public Node focusNode() {
    if (!nodeValid(focusNode)) return null;
    return this.focusNode;
  }

  @Override
  public long focusOffset() {
    if (!nodeValid(focusNode)) return 0;
    return this.focusOffset;
  }

  @Override
  public SelectionDirection direction() {
    return this.direction;
  }

  @Override
  public void setBaseAndExtent(
    Node anchorNode,
    long anchorOffset,
    Node focusNode,
    long focusOffset
  ) {
    // TODO: Need to set range and stuff
    this.anchorNode = anchorNode;
    this.anchorOffset = anchorOffset;
    this.focusNode = focusNode;
    this.focusOffset = focusOffset;
    this.direction = SelectionUtil.determineSelectionDirection(this);
    nodeDocument.changeListener().onSelectionChanged();
  }

  private boolean nodeValid(Node node) {
    return
      node != null
      && node.nodeDocument() == nodeDocument;
  }
  
}
