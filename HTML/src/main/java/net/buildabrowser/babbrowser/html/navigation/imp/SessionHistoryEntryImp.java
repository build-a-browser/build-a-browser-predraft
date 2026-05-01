package net.buildabrowser.babbrowser.html.navigation.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.navigation.SourceSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public class SessionHistoryEntryImp implements SessionHistoryEntry {

  private final DocumentState documentState;
  
  private URI url;

  public SessionHistoryEntryImp(URI url, DocumentState documentState) {
    this.documentState = documentState;
    this.url = url;
  }

  @Override
  public URI url() {
    return this.url;
  }

  @Override
  public void setURL(URI url) {
    this.url = url;
  }

  @Override
  public DocumentState documentState() {
    return this.documentState;
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
