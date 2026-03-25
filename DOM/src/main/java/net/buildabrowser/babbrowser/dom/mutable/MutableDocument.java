package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableDocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;

// MutableDocument has methods not defined in DOM but needed on the document
public interface MutableDocument extends Document, MutableDocumentOrShadowRoot, MutableNode {

  DocumentChangeListener changeListener();

  // Unfortunately, type information lives in HTML which cannot be accessed from here
  // Hence, these return an Object
  Object browsingContext();

  void setBrowsingContext(Object browsingContext);

  Object renderer();

  static MutableDocument create(
    DocumentChangeListener changeListener,
    Object renderer
  ) {
    return new MutableDocumentImp(changeListener, renderer);
  }

  static MutableDocument createForTesting(
    DocumentChangeListener changeListener
  ) {
    return new MutableDocumentImp(changeListener, null);
  }

}
