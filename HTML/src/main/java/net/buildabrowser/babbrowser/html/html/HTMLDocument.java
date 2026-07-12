package net.buildabrowser.babbrowser.html.html;

import java.net.URI;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.imp.HTMLDocumentImp;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.scripting.PlatformObject;

public interface HTMLDocument extends RenderableDocument, Document, PlatformObject {

  void setTitleElement(HTMLElement titleElement);

  Navigable nodeNavigable();

  URI fallbackURL();

  URI baseURL();

  boolean willDeclarativelyRefresh();

  void setWillDeclarativelyRefresh(boolean willDeclarativelyRefresh);

  FocusManager focusManager();

  UANavigableOptions uaNavigableOptions();

  static HTMLDocument create(
    UANavigableOptions uaNavigableOptions,
    BrowsingContext browsingContext
  ) {
    return new HTMLDocumentImp(uaNavigableOptions, browsingContext);
  }

}
