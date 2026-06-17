package net.buildabrowser.babbrowser.html.html;

import java.net.URI;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.imp.HTMLDocumentImp;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.scripting.PlatformObject;

public interface HTMLDocument extends RenderableDocument, Document, PlatformObject {

  void setTitleElement(HTMLElement titleElement);

  Navigable nodeNavigable();

  URI fallbackURL();

  URI baseURL();

  boolean willDeclarativelyRefresh();

  void setWillDeclarativelyRefresh(boolean willDeclarativelyRefresh);

  FocusManager focusManager();

  static HTMLDocument create(BrowsingContext browsingContext) {
    return new HTMLDocumentImp(browsingContext);
  }

}
