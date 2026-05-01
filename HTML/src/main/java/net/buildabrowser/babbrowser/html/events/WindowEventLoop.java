package net.buildabrowser.babbrowser.html.events;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public interface WindowEventLoop extends EventLoop {

  void addNavigable(Navigable navigable);

  Navigable getNavigable(Document relatedDocument);
  
}
