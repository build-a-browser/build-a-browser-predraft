package net.buildabrowser.babbrowser.html.selection;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.selection.imp.SelectionImp;

public interface Selection {
  
  Node anchorNode();

  long anchorOffset();

  Node focusNode();

  long focusOffset();

  // TODO: More fields

  SelectionDirection direction();

  void setBaseAndExtent(
    Node anchorNode,
    long anchorOffset,
    Node focusNode,
    long focusOffset
  );

  static enum SelectionDirection {
    NONE, FORWARD, BACKWARD
  }

  static Selection create(Document nodeDocument) {
    return new SelectionImp(nodeDocument);
  }

}
