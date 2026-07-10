package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.SessionHistoryEntryImp;

public interface SessionHistoryEntry {

  URI url();

  void setURL(URI url);

  DocumentState documentState();

  int step();

  void setStep(int step);

  void populate(
    UANavigableOptions uaNavigableOptions, // Has extra UA-specific arguments
    Navigable navigable,
    SourceSnapshotParams sourceSnapshotParams,
    TargetSnapshotParams targetSnapshotParams,
    UserNavigationInvolvement userInvolvement,
    NavigationParams navigationParams,
    boolean allowPost,
    Runnable completionSteps
  );
  
  default RenderableDocument document() {
    return documentState().document();
  }

  public static SessionHistoryEntry create(URI url, DocumentState documentState) {
    return new SessionHistoryEntryImp(url, documentState);
  }

}
