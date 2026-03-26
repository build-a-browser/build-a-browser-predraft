package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;

public record SessionHistoryEntry(
  DocumentState documentState
) {
  
  public HTMLDocument getDocument() {
    return documentState.document();
  }

  public static SessionHistoryEntry create(DocumentState documentState) {
    return new SessionHistoryEntry(documentState);
  }

}
