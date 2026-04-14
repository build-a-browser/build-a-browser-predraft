package net.buildabrowser.babbrowser.html.navigation;

import java.net.URI;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;

public record SessionHistoryEntry(
  URI url,
  DocumentState documentState
) {
  
  public HTMLDocument getDocument() {
    return documentState.document();
  }

  public static SessionHistoryEntry create(URI url, DocumentState documentState) {
    return new SessionHistoryEntry(url, documentState);
  }

  public void populate(
    UANavigableOptions uaNavigableOptions, // Has extra UA-specific arguments
    Navigable navigable,
    SourceSnapshotParams sourceSnapshotParams,
    UserNavigationInvolvement userInvolvement,
    NavigationParams navigationParams,
    Runnable completionSteps
  ) {
    assert navigationParams == null || navigationParams.response() != null;
    if (navigationParams == null) {
      // TODO: Some extra checks
      if (FetchUtil.isFetchScheme(url.getScheme())) {
        navigationParams = NavigationParams.createByFetching(
          uaNavigableOptions,
          this, navigable, sourceSnapshotParams, userInvolvement);
      } else {
        // TODO
      }
    }

    NavigationParams navigationParams_ = navigationParams; // Java annoyance
    EventLoop.queueGlobalTask(TaskSource.NAVIGATION, navigable.activeWindow(), () -> {
      // TODO: Some stuff
      Document loadedDocument = uaNavigableOptions.loadDocument(navigationParams_);
      documentState.setDocument((HTMLDocument) loadedDocument);

      if (completionSteps != null) {
        completionSteps.run();
      }
    });
  }

}
