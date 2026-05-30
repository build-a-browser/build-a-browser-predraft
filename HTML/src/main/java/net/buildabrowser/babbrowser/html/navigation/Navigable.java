package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.NavigableImp;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public interface Navigable {

  RenderableDocument activeDocument();

  BrowsingContext activeBrowsingContext();

  GlobalObject activeWindow();

  UANavigableOptions uaNavigableOptions();

  void navigate(URI url, NavigateParameters navigateParameters);

  static Navigable create(
    UANavigableOptions uaNavigableOptions,
    DocumentState documentState
  ) {
    assert documentState.document() != null;
    SessionHistoryEntry entry = SessionHistoryEntry.create(
      documentState.document().url(),
      documentState);
    return new NavigableImp(
      uaNavigableOptions,
      entry);
  }
  
}
