package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Document;

public record SessionHistoryEntry(
  DocumentState documentState
) {
  
  public Document getDocument() {
    return documentState.document();
  }

  public static SessionHistoryEntry create(DocumentState documentState) {
    return new SessionHistoryEntry(documentState);
  }

}
