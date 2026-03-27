package net.buildabrowser.babbrowser.dom.mutable;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableDocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;

// MutableDocument has methods not defined in DOM but needed on the document
public interface MutableDocument extends Document, MutableDocumentOrShadowRoot, MutableNode {

  DocumentChangeListener changeListener();

  void setURL(URI url);

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
