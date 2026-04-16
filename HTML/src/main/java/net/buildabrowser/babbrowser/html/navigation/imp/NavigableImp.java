package net.buildabrowser.babbrowser.html.navigation.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.navigation.SourceSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public class NavigableImp implements Navigable {
  
  private final UANavigableOptions uaNavigableOptions;

  private SessionHistoryEntry activeSessionHistory;

  public NavigableImp(
    UANavigableOptions uaNavigableOptions, // UA extension
    // TODO: The various fields required by the spec
    SessionHistoryEntry activeSessionHistory
  ) {
    this.uaNavigableOptions = uaNavigableOptions;
    this.activeSessionHistory = activeSessionHistory;
  }

  @Override
  public HTMLDocument activeDocument() {
    return activeSessionHistory.getDocument();
  }

  @Override
  public BrowsingContext activeBrowsingContext() {
    return activeDocument().browsingContext();
  }

  @Override
  public GlobalObject activeWindow() {
    return activeBrowsingContext().activeWindow();
  }

  @Override
  public UANavigableOptions uaNavigableOptions() {
    return this.uaNavigableOptions;
  }

  @Override
  public void navigate(
    URI url, NavigateParameters navigateParameters
  ) {
    SourceSnapshotParams sourceSnapshotParams = SourceSnapshotParams.snapshot(
      navigateParameters.sourceDocument);
    // TODO: A lot of random steps
    // TODO: Step 14 is navigate to fragment

    EventLoop eventLoop = activeWindow().agent().eventLoop();
    eventLoop.runInParallel(() -> {
      // TODO: A ton of stuff
      DocumentState documentState = DocumentState.create();
      SessionHistoryEntry historyEntry = SessionHistoryEntry.create(url, documentState);
      NavigationParams navigationParams = null;
      historyEntry.populate(
        uaNavigableOptions,
        this,
        sourceSnapshotParams,
        navigateParameters.userInvolvement,
        navigationParams,
        () -> {
          activeSessionHistory = historyEntry; // TODO: Proper way
        });
      uaNavigableOptions.onNavigate(historyEntry.url());
    });
  }

}
