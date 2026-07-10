package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.navigation.imp.TraversableNavigableImp;

public interface TraversableNavigable extends Navigable {

  void traverseHistoryByDelta(int delta, Document sourceDocument);

  void appendSessionHistoryTraversalSteps(Runnable steps);

  HistoryStepResult applyReloadHistoryStep(UserNavigationInvolvement userInvolvement);

  void clearForwardSessionHistory();

  int currentSessionHistoryStep();

  void applyPushReplaceHistoryStep(
    int step,
    NavigationHistoryBehavior historyHandling,
    UserNavigationInvolvement userInvolvement
  );

  static TraversableNavigable create(
    UANavigableOptions uaNavigableOptions,
    DocumentState documentState
  ) {
    assert documentState.document() != null;
    SessionHistoryEntry entry = SessionHistoryEntry.create(
      documentState.document().url(),
      documentState);
    return new TraversableNavigableImp(
      uaNavigableOptions, entry);
  }

}
