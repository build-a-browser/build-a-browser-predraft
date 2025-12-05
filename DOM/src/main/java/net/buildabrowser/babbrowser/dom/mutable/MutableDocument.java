package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableDocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;

public interface MutableDocument extends Document, MutableDocumentOrShadowRoot, MutableNode {

  DocumentChangeListener changeListener();

  static MutableDocument create(DocumentChangeListener changeListener) {
    return new MutableDocumentImp(changeListener);
  }

}
