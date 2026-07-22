package net.buildabrowser.babbrowser.html.navigation.imp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.ChangingNavigableContinuationState;
import net.buildabrowser.babbrowser.html.navigation.HistoryStepResult;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.NavigationSteps;
import net.buildabrowser.babbrowser.html.navigation.NavigationType;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryTraversalParallelQueue;
import net.buildabrowser.babbrowser.html.navigation.SourceSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.TargetSnapshotParams;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;
import net.buildabrowser.babbrowser.html.navigation.imp.util.DocumentNavigationUtil;

public class HistoryStep {

  private final TraversableNavigableImp traversable;
  private final SessionHistoryTraversalParallelQueue sessionHistoryTraversalQueue;

  private boolean runningNestedApplyHistoryStep = false;

  public HistoryStep(
    TraversableNavigableImp traversable,
    SessionHistoryTraversalParallelQueue sessionHistoryTraversalQueue
  ) {
    this.traversable = traversable;
    this.sessionHistoryTraversalQueue = sessionHistoryTraversalQueue;
  }

  // TODO: This method, based on the spec, is absolutely massive
  // and should probably be broken down more
  public HistoryStepResult applyHistoryStep(
    int step,
    boolean checkForCancelation,
    SourceSnapshotParams sourceSnapshotParams,
    Navigable initiatorToCheck,
    UserNavigationInvolvement userInvolvement,
    NavigationType navigationType
  ) {
    // TODO: Assert running on traversal queue
    int targetStep = getUsedStep(step);
    if (initiatorToCheck != null) {
      assert sourceSnapshotParams != null;
      // TODO: Actually perform the check
    }

    // TODO: Check for cancelation

    List<Navigable> changingNavigables
      = getAllNavigablesFiltered(targetStep, false);
    // TODO: Handle navigables that only need update
    for (Navigable navigable: changingNavigables) {
      SessionHistoryEntry targetEntry = navigable.getTargetHistoryEntry(targetStep);
      navigable.setCurrentSessionHistoryEntry(targetEntry);
      // TODO: Queue task to clear some navigation data
      navigable.setOngoingNavigation("traversal");
    }

    int totalChangeJobs = changingNavigables.size();
    AtomicInteger completedChangeJobs = new AtomicInteger(0);
    List<ChangingNavigableContinuationState> changingNavigableContinuations = new LinkedList<>();
    // TODO: Continuations
    for (Navigable navigable: changingNavigables) {
      EventLoop.queueGlobalTask(
        TaskSource.NAVIGATION, navigable.activeWindow(),
        () -> {
          // If the document only needs updated, put it on the update queue
          // Otherwise, populate it, and then put it on the update queue
          SessionHistoryEntry displayedEntry = navigable.activeSessionHistoryEntry();
          SessionHistoryEntry targetEntry = navigable.currentSessionHistoryEntry();
          ChangingNavigableContinuationState changingNavigableContinuation
            = new ChangingNavigableContinuationState();
          changingNavigableContinuation.displayedDocument = displayedEntry.document();
          changingNavigableContinuation.targetEntry = targetEntry;
          changingNavigableContinuation.navigable = navigable;
          changingNavigableContinuation.updateOnly = false;
          if (
            displayedEntry == targetEntry
            && !targetEntry.documentState().reloadPending()
          ) {
            changingNavigableContinuation.updateOnly = true;
            changingNavigableContinuations.add(changingNavigableContinuation);
            return;
          }
          //

          assertNavigationTypeInvariants(
            navigationType, displayedEntry, targetEntry);

          // TODO: Fire events
          Runnable afterDocumentPopulated = () -> {
            // TODO: Other steps
            if (targetEntry.document() == null) {
              changingNavigableContinuation.updateOnly = true;
            }
            // TODO: Set API state, set name
            changingNavigableContinuations.add(changingNavigableContinuation);
          };

          ensureDocumentPopulated(
            sourceSnapshotParams, userInvolvement,
            navigable, targetEntry, afterDocumentPopulated);
        });
    }

    Set<Navigable> navigablesThatMustWaitBeforeHandlingSyncNavigation = new HashSet<>();
    while (completedChangeJobs.get() != totalChangeJobs) {
      jumpSyncTasks(navigablesThatMustWaitBeforeHandlingSyncNavigation);

      if (changingNavigableContinuations.isEmpty()) continue;
      ChangingNavigableContinuationState changingNavigableContinuation
        = changingNavigableContinuations.remove(0);

      RenderableDocument displayedDocument = changingNavigableContinuation.displayedDocument;
      SessionHistoryEntry targetEntry = changingNavigableContinuation.targetEntry;
      Navigable navigable = changingNavigableContinuation.navigable;
      navigablesThatMustWaitBeforeHandlingSyncNavigation.add(navigable);
      
      // Steps to activate document, if needed, and then apply any updates
      Set<Integer> steps = traversable.getAllUsedHistorySteps();
      int scriptHistoryLength = steps.size();
      int scriptHistoryIndex = indexOf(steps, targetStep);
      assert scriptHistoryIndex != -1;
      // TODO: Get entries for navigation API
      Runnable afterPotentialUnloads = () -> {
        SessionHistoryEntry previousEntry = navigable.activeSessionHistoryEntry();
        if (!changingNavigableContinuation.updateOnly) {
          navigable.activateHistoryEntry(targetEntry);
        }
        Runnable updateDocument = () -> DocumentNavigationUtil.updateDocumentForHistoryStepApplication(
          targetEntry.document(), targetEntry, changingNavigableContinuation.updateOnly,
          scriptHistoryLength, scriptHistoryIndex, navigationType,
          // TODO: Navigation API entries
          null, previousEntry);
        if (targetEntry.document() == displayedDocument) {
          updateDocument.run();
        } else {
          EventLoop.queueGlobalTask(
            TaskSource.NAVIGATION,
            // TODO: Better way to get the global object
            targetEntry.document().browsingContext().realm().globalObject(),
            updateDocument);
        }
        completedChangeJobs.incrementAndGet();
      };

      // Unload previous document if needs unloaded
      // (Before activating/updating current document)
      if (
        changingNavigableContinuation.updateOnly
        || targetEntry.document() == displayedDocument
      ) {
        navigable.setOngoingNavigation(null);
        EventLoop.queueGlobalTask(
          TaskSource.NAVIGATION, navigable.activeWindow(),
          afterPotentialUnloads);
      } else {
        assert navigationType != null;
        deactivateDocument(
          displayedDocument, userInvolvement, targetEntry,
          navigationType, afterPotentialUnloads, /* nospec */ navigable);
      }
    }

    // TODO: More stuff
    traversable.currentSessionHistoryStep = step;
    return HistoryStepResult.APPLIED;
  }

  // Stuff split out from the above method

  private void assertNavigationTypeInvariants(
    NavigationType navigationType,
    SessionHistoryEntry displayedEntry,
    SessionHistoryEntry targetEntry
  ) {
    switch (navigationType) {
      case NavigationType.RELOAD -> {
        assert targetEntry.documentState().reloadPending(); }
      case NavigationType.TRAVERSE -> {
        assert targetEntry.documentState().everPopulated(); }
      case NavigationType.REPLACE -> {
        assert
          targetEntry.step() == displayedEntry.step()
          && !targetEntry.documentState().everPopulated(); }
      case NavigationType.PUSH -> {
        assert
          targetEntry.step() == displayedEntry.step() + 1
          && !targetEntry.documentState().everPopulated(); }
    }
  }

  private void jumpSyncTasks(
    Set<Navigable> navigablesThatMustWaitBeforeHandlingSyncNavigation
  ) {
    if (!runningNestedApplyHistoryStep) {
      List<NavigationSteps> stepList = sessionHistoryTraversalQueue.asList();
      int stepIndex = 0;
      while (stepIndex < stepList.size()) {
        NavigationSteps steps = stepList.get(stepIndex);
        if (
          !steps.synchronous()
          || navigablesThatMustWaitBeforeHandlingSyncNavigation.contains(steps.targetNavigable())
        ) {
          stepIndex++;
          continue;
        }
        stepList.remove(stepIndex);
        this.runningNestedApplyHistoryStep = true;
        steps.algorithm().run();
        this.runningNestedApplyHistoryStep = false;
      }
    }
  }

  private void ensureDocumentPopulated(
    SourceSnapshotParams sourceSnapshotParams,
    UserNavigationInvolvement userInvolvement,
    Navigable navigable,
    SessionHistoryEntry targetEntry,
    Runnable afterDocumentPopulated
  ) {
    if (
      targetEntry.document() == null
      || targetEntry.documentState().reloadPending()
    ) {
      TargetSnapshotParams targetSnapshotParams = TargetSnapshotParams.snapshot(navigable);
      SourceSnapshotParams potentiallyTargetSpecificSourceSnapshotParams = sourceSnapshotParams != null ?
        sourceSnapshotParams :
        SourceSnapshotParams.snapshot(navigable.activeDocument());
      // Spec Bug: The spec does this in the opposite order, meaning allowPOST would always be false
      // We do it in this order
      boolean allowPOST = targetEntry.documentState().reloadPending();
      targetEntry.documentState().setReloadPending(false);
      traversable.activeWindow().agent().eventLoop().runInParallel(() -> {
        targetEntry.populate(
          traversable.uaNavigableOptions(),
          navigable,
          potentiallyTargetSpecificSourceSnapshotParams,
          targetSnapshotParams,
          userInvolvement,
          null,
          allowPOST,
          afterDocumentPopulated);
      });
    } else {
      afterDocumentPopulated.run();
    }
  }

  // Stuff not split out

  // TODO: Move somewhere more suitable
  private void deactivateDocument(
    RenderableDocument displayedDocument,
    UserNavigationInvolvement userNavigationInvolvement,
    SessionHistoryEntry targetEntry,
    NavigationType navigationType,
    Runnable afterPotentialUnloads,
    // Spec Bug (?) or a bug in my code, not sure
    // During a reload, the current and active session history entry are the same.
    // Upon document population, the target entry (current entry)'s document is updated
    // Since the current entry and active entry are the same, the active entry's reference to
    // the old document is erased
    // displayedDocument's node navigable is defined as the navigable whose active entry's active document
    // is the displayedDocument. But since that reference no longer exists, it is null
    // Therefore, the step "Let navigable be displayedDocument's node navigable" would yield null
    // As such, we do this instead:
    // NOSPEC: Accept the navigable as a parameter
    Navigable navigable
  ) {
    // Navigable navigable = displayedDocument.nodeNavigable();
    // TODO: Handle view transition
    navigable.setOngoingNavigation(null);
    // TODO: Unload the document
    afterPotentialUnloads.run();
  }

  private int indexOf(Set<Integer> steps, int targetStep) {
    int stepIndex = 0;
    for (Integer step: steps) {
      if (step == targetStep) return stepIndex;
      stepIndex++;
    }

    return -1;
  }

  private int getUsedStep(int step) {
    Set<Integer> steps = traversable.getAllUsedHistorySteps();
    int greatestStep = 0;
    for (int compStep: steps) {
      if (compStep > step) continue;
      greatestStep = Math.max(greatestStep, compStep);
    }
    
    return greatestStep;
  }

  private List<Navigable> getAllNavigablesFiltered(
    int targetStep, boolean needsUpdate
  ) {
    List<Navigable> results = new ArrayList<>();
    List<Navigable> navigablesToCheck = new ArrayList<>();
    navigablesToCheck.add(traversable);
    while (!navigablesToCheck.isEmpty()) {
      Navigable navigable = navigablesToCheck.remove(0);
      SessionHistoryEntry targetEntry = navigable.getTargetHistoryEntry(targetStep);
      boolean isUpdateOnly =
        targetEntry == navigable.currentSessionHistoryEntry()
        && !targetEntry.documentState().reloadPending();
      if (isUpdateOnly == needsUpdate) {
        results.add(navigable);
      }
      // TODO: Extend with child navigables
    }
    
    return results;
  }
  
}
