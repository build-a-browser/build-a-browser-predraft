package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.html.html.imp.HTMLDocumentImp;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public interface HTMLDocument extends MutableDocument {
  
  BrowsingContext browsingContext();

  DocumentRenderer renderer();

  static HTMLDocument create(
    BrowsingContext browsingContext,
    UAHTMLDocumentOptions documentOptions
  ) {
    return new HTMLDocumentImp(browsingContext, documentOptions);
  }

}
