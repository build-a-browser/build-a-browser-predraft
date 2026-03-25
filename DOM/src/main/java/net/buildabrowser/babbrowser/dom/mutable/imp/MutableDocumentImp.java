package net.buildabrowser.babbrowser.dom.mutable.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.mutable.MutableStyleSheetList;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;

public class MutableDocumentImp extends MutableNodeImp implements MutableDocument {

  private final MutableStyleSheetList styleSheets;
  private final DocumentChangeListener changeListener;
  private final Object renderer;

  private Object browsingContext;

  public MutableDocumentImp(
    DocumentChangeListener changeListener,
    Object renderer
  ) {
    this.changeListener = changeListener;
    this.styleSheets = MutableStyleSheetList.create(
      styleSheet -> changeListener.onStylesheetAdded(styleSheet));
    this.renderer = renderer;
  }

  @Override
  public MutableDocument ownerDocument() {
    return this;
  }

  @Override
  public MutableStyleSheetList styleSheets() {
    return this.styleSheets;
  }

  //

  @Override
  public DocumentChangeListener changeListener() {
    return this.changeListener;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Node child: childNodes()) {
      builder.append(child.toString());
    }
    
    return builder.toString();
  }

  @Override
  public Object browsingContext() {
    return this.browsingContext;
  }

  @Override
  public void setBrowsingContext(Object browsingContext) {
    this.browsingContext = browsingContext;
  }

  @Override
  public Object renderer() {
    return this.renderer;
  }

}
