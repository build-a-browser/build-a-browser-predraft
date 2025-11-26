package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.Node;

public interface DocumentChangeListener {
  
  void onNodeAdded(Node node);

  void onNodeRemoved(Node node);

}
