package net.buildabrowser.babbrowser.dom.mutable.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableStyleSheetList;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;

public class MutableDocumentImp implements MutableDocument {

  private final List<Node> children = new LinkedList<>();
  private final MutableStyleSheetList styleSheets = MutableStyleSheetList.create();
  private final DocumentChangeListener changeListener;

  private Object context;

  public MutableDocumentImp(DocumentChangeListener changeListener) {
    this.changeListener = changeListener;
  }

  @Override
  public void onNodeAdded(Node node) {
    changeListener.onNodeAdded(node);
  }

  @Override
  public void onNodeRemoved(Node node) {
    changeListener.onNodeRemoved(node);
  }

  @Override
  public void setContext(Object context) {
    this.context = context;
  }

  @Override
  public Object getContext() {
    return this.context;
  }

  @Override
  public MutableStyleSheetList styleSheets() {
    return this.styleSheets;
  }

  @Override
  public List<Node> children() {
    return this.children;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Node child: children) {
      builder.append(child.toString());
    }
    
    return builder.toString();
  }

  @Override
  public Document immutable() {
    return Document.create(
      children.stream().map(e -> e instanceof MutableNode n ? n.immutable() : e).toList(),
      styleSheets.immutable());
  }

}
