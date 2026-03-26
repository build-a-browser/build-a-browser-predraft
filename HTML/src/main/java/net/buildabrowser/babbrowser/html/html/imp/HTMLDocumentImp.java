package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.dom.mutable.imp.MutableDocumentImp;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.UAHTMLDocumentOptions;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public class HTMLDocumentImp extends MutableDocumentImp implements HTMLDocument {

  private final BrowsingContext browsingContext;
  private final DocumentRenderer renderer;

  public HTMLDocumentImp(
    BrowsingContext browsingContext,
    UAHTMLDocumentOptions documentOptions
  ) {
    super(documentOptions.changeListener());
    this.browsingContext = browsingContext;
    this.renderer = documentOptions.renderer();
  }

  @Override
  public BrowsingContext browsingContext() {
    return this.browsingContext;
  }

  @Override
  public DocumentRenderer renderer() {
    return this.renderer;
  }
  
}
