package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public record Navigable(
  // TODO: The various fields required by the spec
  SessionHistoryEntry activeSessionHistory
) {

  public HTMLDocument activeDocument() {
    return activeSessionHistory.getDocument();
  }

  public GlobalObject activeWindow() {
    return activeDocument().browsingContext().activeWindow();
  }

  public static Navigable create(SessionHistoryEntry activeSessionHistory) {
    return new Navigable(activeSessionHistory);
  }
  
}
