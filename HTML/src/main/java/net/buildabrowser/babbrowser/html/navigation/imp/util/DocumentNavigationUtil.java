package net.buildabrowser.babbrowser.html.navigation.imp.util;

import java.util.List;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.NavigationType;
import net.buildabrowser.babbrowser.html.navigation.SessionHistoryEntry;

public final class DocumentNavigationUtil {
  
  private DocumentNavigationUtil() {}

  public static void updateDocumentForHistoryStepApplication(
    RenderableDocument document,
    SessionHistoryEntry entry,
    boolean doNotReactivate,
    int scriptHistoryLength,
    int scriptHistoryIndex,
    NavigationType navigationType,
    List<SessionHistoryEntry> entriesForNavigationAPI,
    SessionHistoryEntry previousEntryForActivation
  ) {
    // TODO: Hacky way to make sure fragment event listener runs
    document.setURL(entry.url());
    // TODO: Implement
  }

}
