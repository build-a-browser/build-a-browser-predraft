package net.buildabrowser.babbrowser.dom.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class DocumentImp extends NodeImp implements Document {

  private static final DocumentChangeListener NOOP_CHANGE_LISTENER =
    new AbstractDocumentChangeListener(null) {};

  private final StyleSheetList styleSheets;
  private final DocumentChangeListener changeListener;
  
  private URI url = CommonUtil.rethrow(() -> URI.create("about:blank"));

  public DocumentImp() {
    this(NOOP_CHANGE_LISTENER);
  }

  public DocumentImp(DocumentChangeListener changeListener) {
    this.changeListener = changeListener;
    this.styleSheets = StyleSheetList.create(
      styleSheet -> changeListener().onStylesheetAdded(styleSheet));
  }

  @Override
  public Document nodeDocument() {
    return this;
  }

  @Override
  public StyleSheetList styleSheets() {
    return this.styleSheets;
  }

  //

  @Override
  public DocumentChangeListener changeListener() {
    return this.changeListener;
  }

  @Override
  public URI url() {
    return this.url;
  }

  @Override
  public void setURL(URI url) {
    this.url = url;
  }

}
