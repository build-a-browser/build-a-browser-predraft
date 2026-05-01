package net.buildabrowser.babbrowser.html.events;

import net.buildabrowser.babbrowser.dom.Document;

public record Task(
  Runnable steps,
  TaskSource source,
  Document document
  // TODO: Script environment settings object
) {

  public boolean isRunnable() {
    // TODO: Implement this once navigation is a thing
    return true;
  }
  
}
