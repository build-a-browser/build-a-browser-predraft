package net.buildabrowser.babbrowser.html.navigation.util;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;
import net.buildabrowser.babbrowser.html.navigation.TraversableNavigable;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;

public final class TraversableUtil {
  
  private TraversableUtil() {}

  public static TraversableNavigable createNewTopLevelTraversable(
    UANavigableOptions uaNavigableOptions
  ) {
    HTMLDocument document = BrowsingContext.create(uaNavigableOptions).activeDocument();
    DocumentState documentState = DocumentState.create();
    documentState.setDocument(document);
    // TODO: Support aux context
    TraversableNavigable traversable = TraversableNavigable.create(uaNavigableOptions, documentState);
    SessionHistoryEntry initialHistoryEntry = traversable.activeSessionHistoryEntry();
    initialHistoryEntry.setStep(0);
    traversable.getSessionHistoryEntries().add(initialHistoryEntry);
    // TODO: Some other stuff
    return traversable;
  }

}
