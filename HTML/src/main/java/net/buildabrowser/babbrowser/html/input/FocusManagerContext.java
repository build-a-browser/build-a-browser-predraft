package net.buildabrowser.babbrowser.html.input;

import net.buildabrowser.babbrowser.dom.Node;

public interface FocusManagerContext {

  void onFocusChanged(Node oldFocused, Node newFocused);
  
  FocusIgnore getIgnore(Node node);

  static enum FocusIgnore {
    NONE, SELF, TREE;
  }

}
