package net.buildabrowser.babbrowser.debugger.core;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface DebuggerDocumentChangeListener extends DocumentChangeListener {
  
  boolean onFragmentEvent(
    Node node, DebugBox box, DebugFragment fragment,
    Event event, boolean allowDefault
  );

}
