package net.buildabrowser.babbrowser.html.navigation.imp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.HistoryStepResult;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigationHistoryBehavior;
import net.buildabrowser.babbrowser.html.navigation.NavigationSteps;
import net.buildabrowser.babbrowser.html.navigation.NavigationType;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryTraversalParallelQueue;
import net.buildabrowser.babbrowser.html.navigation.SourceSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.TraversableNavigable;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public class TraversableNavigableImp extends NavigableImp implements TraversableNavigable {

  private final List<SessionHistoryEntry> sessionHistoryEntries = new LinkedList<>();
  private final SessionHistoryTraversalParallelQueue sessionHistoryTraversalQueue;
  private final HistoryStep historyStep;

  // TODO: Avoid package-private field
  int currentSessionHistoryStep = 0;
  
  public TraversableNavigableImp(
    UANavigableOptions uaNavigableOptions,
    SessionHistoryEntry entry
  ) {
    super(uaNavigableOptions, entry, null);
    this.sessionHistoryTraversalQueue = SessionHistoryTraversalParallelQueue.create(
      entry.document().browsingContext().activeWindow());
    this.historyStep = new HistoryStep(this, sessionHistoryTraversalQueue);
  }

  @Override
  public void traverseHistoryByDelta(
    int delta, Document sourceDocument
  ) {
    SourceSnapshotParams sourceSnapshotParams = null;
    Navigable initiatorToCheck = null;
    UserNavigationInvolvement userInvolvement = UserNavigationInvolvement.BROWSER_UI;
    // TODO: Generalize to all documents
    if (sourceDocument instanceof HTMLDocument htmlDocument) {
      sourceSnapshotParams = SourceSnapshotParams.snapshot(sourceDocument);
      initiatorToCheck = htmlDocument.nodeNavigable();
      userInvolvement = UserNavigationInvolvement.NONE;
    }

    SourceSnapshotParams sourceSnapshotParams_ = sourceSnapshotParams;
    Navigable initiatorToCheck_ = initiatorToCheck;
    UserNavigationInvolvement userInvolvement_ = userInvolvement;
    appendSessionHistoryTraversalSteps(() -> {
      List<Integer> allSteps = List.copyOf(getAllUsedHistorySteps());
      int currentStepIndex = allSteps.indexOf(currentSessionHistoryStep);
      assert currentStepIndex != -1;
      int targetStepIndex = currentStepIndex + delta;
      if (
        targetStepIndex < 0
        || targetStepIndex >= allSteps.size()
      ) return;
      applyTraverseHistoryStep(
        allSteps.get(targetStepIndex),
        sourceSnapshotParams_, initiatorToCheck_, userInvolvement_);
    });
  }

  @Override
  public void appendSessionHistoryTraversalSteps(Runnable steps) {
    NavigationSteps navSteps = new NavigationSteps(steps, null);
    sessionHistoryTraversalQueue.queue(navSteps);
  }

  @Override
  public HistoryStepResult applyReloadHistoryStep(UserNavigationInvolvement userInvolvement) {
    int step = currentSessionHistoryStep;
    return historyStep.applyHistoryStep(
      step, true, null, null,
      userInvolvement, NavigationType.RELOAD);
  }

  @Override
  public void applyPushReplaceHistoryStep(
    int step,
    NavigationHistoryBehavior historyHandling,
    UserNavigationInvolvement userInvolvement
  ) {
    historyStep.applyHistoryStep(
      step, false, null, null,
      userInvolvement, historyHandling.toNavigationType());
  }

  @Override
  public List<SessionHistoryEntry> getSessionHistoryEntries() {
    return this.sessionHistoryEntries;
  }

  @Override
  public void clearForwardSessionHistory() {
    // TODO: Assert queue
    int step = currentSessionHistoryStep;
    // TODO: Needs to have set semantic that item cannot be added twice
    // (but Set does not have ListIterator)
    Set<List<SessionHistoryEntry>> entryLists = new HashSet<>();
    entryLists.add(sessionHistoryEntries);
    // TODO: This will cause a ConcurrentModificationException
    for (List<SessionHistoryEntry> entryList: entryLists) {
      ListIterator<SessionHistoryEntry> entryIt = entryList.listIterator();
      while (entryIt.hasNext()) {
        if (entryIt.next().step() > step) {
          entryIt.remove();
        }
      }
      // TODO: Handle nested histories
    }
  }

  @Override
  public int currentSessionHistoryStep() {
    return this.currentSessionHistoryStep;
  }

  // TODO: Avoid package-private method
  Set<Integer> getAllUsedHistorySteps() {
    // TODO: Assert running queue
    Set<Integer> steps = new TreeSet<>(Integer::compare);
    List<List<SessionHistoryEntry>> entryLists = new ArrayList<>();
    entryLists.add(sessionHistoryEntries);
    while (!entryLists.isEmpty()) {
      List<SessionHistoryEntry> entryList = entryLists.remove(0);
      for (SessionHistoryEntry entry: entryList) {
        steps.add(entry.step());
        // TODO: Nested histories
      }
    }

    return steps;
  }

  private HistoryStepResult applyTraverseHistoryStep(
    int step,
    SourceSnapshotParams sourceSnapshotParams,
    Navigable initiatorToCheck,
    UserNavigationInvolvement userInvolvement
  ) {
    return historyStep.applyHistoryStep(
      step, true, sourceSnapshotParams, initiatorToCheck,
      userInvolvement, NavigationType.TRAVERSE);
  }

}
