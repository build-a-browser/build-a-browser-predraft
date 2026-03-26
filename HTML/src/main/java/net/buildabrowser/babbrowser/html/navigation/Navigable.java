package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public record Navigable(
  // TODO: The various fields required by the spec
  SessionHistoryEntry activeSessionHistory
) {

  public Document activeDocument() {
    return activeSessionHistory.getDocument();
  }

  public GlobalObject activeWindow() {
    return ((BrowsingContext) ((MutableDocument) activeDocument()).browsingContext()).window();
  }

  public static Navigable create(SessionHistoryEntry activeSessionHistory) {
    return new Navigable(activeSessionHistory);
  }
  
}
