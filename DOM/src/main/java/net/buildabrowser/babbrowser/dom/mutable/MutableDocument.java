package net.buildabrowser.babbrowser.dom.mutable;

import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.mutable.MutableDocumentOrShadowRoot;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;

public interface MutableDocument extends Document, MutableDocumentOrShadowRoot {

  List<Node> children();
  
  static MutableDocument create() {
    return new MutableDocumentImp();
  }

  Document immutable();

}
