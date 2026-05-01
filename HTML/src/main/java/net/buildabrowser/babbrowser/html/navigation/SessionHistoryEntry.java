package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.SessionHistoryEntryImp;

public interface SessionHistoryEntry {

  URI url();

  void setURL(URI url);

  DocumentState documentState();

  void populate(
    UANavigableOptions uaNavigableOptions, // Has extra UA-specific arguments
    Navigable navigable,
    SourceSnapshotParams sourceSnapshotParams,
    UserNavigationInvolvement userInvolvement,
    NavigationParams navigationParams,
    Runnable completionSteps
  );
  
  default HTMLDocument getDocument() {
    return documentState().document();
  }

  public static SessionHistoryEntry create(URI url, DocumentState documentState) {
    return new SessionHistoryEntryImp(url, documentState);
  }

}
