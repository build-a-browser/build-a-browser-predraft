package net.buildabrowser.babbrowser.html.html;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.imp.HTMLDocumentImp;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.scripting.PlatformObject;

public interface HTMLDocument extends Document, Invalidatable, PlatformObject {
  
  BrowsingContext browsingContext();

  DocumentRenderer renderer();

  void attachRenderer(DocumentRenderer renderer);

  URI fallbackURL();

  URI baseURL();

  static HTMLDocument create(BrowsingContext browsingContext) {
    return new HTMLDocumentImp(browsingContext);
  }

}
