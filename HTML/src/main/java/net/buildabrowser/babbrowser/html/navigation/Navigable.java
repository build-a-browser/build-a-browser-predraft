package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.NavigableImp;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public interface Navigable {

  Navigable parent();

  RenderableDocument activeDocument();

  BrowsingContext activeBrowsingContext();

  GlobalObject activeWindow();

  UANavigableOptions uaNavigableOptions();

  void navigate(URI url, NavigateParameters navigateParameters);
  
  // TODO: More params
  void reload(UserNavigationInvolvement userInvolvement);

  // Stuff primarily used by the navigation algorithm

  SessionHistoryEntry getTargetHistoryEntry(int step);

  void activateHistoryEntry(SessionHistoryEntry entry);

  SessionHistoryEntry activeSessionHistoryEntry();

  SessionHistoryEntry currentSessionHistoryEntry();

  void setCurrentSessionHistoryEntry(SessionHistoryEntry entry);

  void setOngoingNavigation(String id);

  List<SessionHistoryEntry> getSessionHistoryEntries();

  TraversableNavigable traversable();

  static Navigable create(
    UANavigableOptions uaNavigableOptions,
    DocumentState documentState,
    Navigable parent
  ) {
    assert documentState.document() != null;
    SessionHistoryEntry entry = SessionHistoryEntry.create(
      documentState.document().url(),
      documentState);
    return new NavigableImp(
      uaNavigableOptions, entry, parent);
  }
  
}
