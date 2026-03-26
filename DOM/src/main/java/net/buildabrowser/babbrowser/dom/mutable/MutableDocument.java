package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableDocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;

// MutableDocument has methods not defined in DOM but needed on the document
public interface MutableDocument extends Document, MutableDocumentOrShadowRoot, MutableNode {

  DocumentChangeListener changeListener();

  static MutableDocument create(
    DocumentChangeListener changeListener
  ) {
    return new MutableDocumentImp(changeListener);
  }

  static MutableDocument createForTesting(
    DocumentChangeListener changeListener
  ) {
    return new MutableDocumentImp(changeListener);
  }

}
