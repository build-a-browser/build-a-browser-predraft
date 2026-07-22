package net.buildabrowser.babbrowser.html.navigation.imp;

import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigateParameters;
import net.buildabrowser.babbrowser.html.navigation.NavigationHistoryBehavior;
import net.buildabrowser.babbrowser.html.navigation.NavigationParams;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.navigation.SourceSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.TargetSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.TraversableNavigable;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public class NavigableImp implements Navigable {
  
  private final UANavigableOptions uaNavigableOptions;
  private final Navigable parent;

  @SuppressWarnings("unused")
  private String ongoingNavigation;

  private SessionHistoryEntry currentSessionHistoryEntry;
  private SessionHistoryEntry activeSessionHistory;

  public NavigableImp(
    UANavigableOptions uaNavigableOptions, // UA extension
    SessionHistoryEntry entry,
    Navigable parent
  ) {
    this.uaNavigableOptions = uaNavigableOptions;
    this.currentSessionHistoryEntry = entry;
    this.activeSessionHistory = entry;
    this.parent = parent;
    // TODO: Set initial visibility state
  }

  @Override
  public Navigable parent() {
    return this.parent;
  }

  @Override
  public RenderableDocument activeDocument() {
    return activeSessionHistory.document();
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
    if (navigateParameters.historyHandling.equals(NavigationHistoryBehavior.AUTO)) {
      // TODO: Check origin
      navigateParameters.historyHandling = url.equals(activeDocument().url()) ?
        NavigationHistoryBehavior.REPLACE :
        NavigationHistoryBehavior.PUSH;
    }
    // TODO: Check must be a replace
    // TODO: Step 15 is navigate to fragment

    TargetSnapshotParams targetSnapshotParams = TargetSnapshotParams.snapshot(this);
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
        targetSnapshotParams,
        navigateParameters.userInvolvement,
        navigationParams,
        true,
        () -> {
          closeActiveRenderer();
          traversable().appendSessionHistoryTraversalSteps(() -> {
            finalizeACrossDocumentNavigation(
              navigateParameters.historyHandling,
              navigateParameters.userInvolvement,
              historyEntry);
          });
        });
      uaNavigableOptions.onNavigate(historyEntry.url());
    });
  }

  private void closeActiveRenderer() {
    DocumentRenderer renderer = activeSessionHistory.document().renderer();
    if (renderer == null) return;
    CommonUtil.rethrowV(() -> renderer.close());
  }

  @Override
  public void reload(
    UserNavigationInvolvement userInvolvement
  ) {
    // TODO: Handle event
    activeSessionHistory.documentState().setReloadPending(true);
    TraversableNavigable traversable = traversable();
    traversable.appendSessionHistoryTraversalSteps(
      () -> traversable.applyReloadHistoryStep(userInvolvement));
  }

  private void finalizeACrossDocumentNavigation(
    NavigationHistoryBehavior historyHandling,
    UserNavigationInvolvement userInvolvement,
    SessionHistoryEntry historyEntry
  ) {
    // TODO: Assert queue, delay load events
    if (historyEntry.document() == null) return;
    // TODO: Set target name if needed
    SessionHistoryEntry entryToReplace = historyHandling.equals(NavigationHistoryBehavior.REPLACE) ?
      activeSessionHistory : null;
    TraversableNavigable traversable = traversable();
    int targetStep = -1;
    List<SessionHistoryEntry> targetEntries = getSessionHistoryEntries();
    if (entryToReplace == null) {
      traversable.clearForwardSessionHistory();
      targetStep = traversable.currentSessionHistoryStep() + 1;
      historyEntry.setStep(targetStep);
      targetEntries.add(historyEntry);
    } else {
      int replaceIndex = targetEntries.indexOf(entryToReplace);
      assert replaceIndex != -1;
      targetEntries.set(replaceIndex, historyEntry);
      historyEntry.setStep(entryToReplace.step());
      // TODO: Update navigation API key
      targetStep = traversable.currentSessionHistoryStep();
    }
    traversable.applyPushReplaceHistoryStep(targetStep, historyHandling, userInvolvement);
  }

  @Override
  public List<SessionHistoryEntry> getSessionHistoryEntries() {
    throw new UnsupportedOperationException("Nested histories not implemented");
  }

  @Override
  public TraversableNavigable traversable() {
    Navigable navigable = this;
    while (navigable.parent() != null) {
      navigable = navigable.parent();
    }
    return (TraversableNavigable) navigable;
  }

  @Override
  public SessionHistoryEntry getTargetHistoryEntry(int step) {
    List<SessionHistoryEntry> entries = getSessionHistoryEntries();
    SessionHistoryEntry greatestItem = null;
    for (SessionHistoryEntry entry: entries) {
      if (
        entry.step() <= step
        && (greatestItem == null || entry.step() > greatestItem.step())
      ) {
        greatestItem = entry;
      }
    }
    assert greatestItem != null;
    return greatestItem;
  }

  @Override
  public SessionHistoryEntry activeSessionHistoryEntry() {
    return this.activeSessionHistory;
  }

  @Override
  public void activateHistoryEntry(SessionHistoryEntry entry) {
    // TODO: Other steps
    this.activeSessionHistory = entry;
    uaNavigableOptions.onNavigate(entry.url());
    uaNavigableOptions.requestRepaint();
  }

  @Override
  public SessionHistoryEntry currentSessionHistoryEntry() {
    return this.currentSessionHistoryEntry;
  }

  @Override
  public void setCurrentSessionHistoryEntry(SessionHistoryEntry entry) {
    this.currentSessionHistoryEntry = entry;
  }

  @Override
  public void setOngoingNavigation(String newValue) {
    this.ongoingNavigation = newValue;
    // TODO: Inform the navigation API
  }

}
