package net.buildabrowser.babbrowser.dom.mutable;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableDocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;

public interface MutableDocument extends Document, MutableDocumentOrShadowRoot, MutableNode {

  List<Node> children();

  void onNodeAdded(Node node);

  void onNodeRemoved(Node node);

  Document immutable();

  static MutableDocument create(DocumentChangeListener changeListener) {
    return new MutableDocumentImp(changeListener);
  }

}
