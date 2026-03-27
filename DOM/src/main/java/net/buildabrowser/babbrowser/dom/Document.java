package net.buildabrowser.babbrowser.dom;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.DocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.imp.DocumentImp;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface Document extends Node, DocumentOrShadowRoot {

  URI url();

  // Extensions

  void setURL(URI url);

  DocumentChangeListener changeListener();

  static Document create(DocumentChangeListener documentChangeListener) {
    return new DocumentImp(documentChangeListener);
  }

}
