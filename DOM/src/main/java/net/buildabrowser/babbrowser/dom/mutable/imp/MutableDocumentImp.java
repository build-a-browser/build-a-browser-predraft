package net.buildabrowser.babbrowser.dom.mutable.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.mutable.MutableStyleSheetList;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;

public record MutableDocumentImp(List<Node> children, MutableStyleSheetList styleSheets) implements MutableDocument {

  public MutableDocumentImp() {
    this(new LinkedList<>(), MutableStyleSheetList.create());
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
    return Document.create(children, styleSheets.immutable());
  }

}
