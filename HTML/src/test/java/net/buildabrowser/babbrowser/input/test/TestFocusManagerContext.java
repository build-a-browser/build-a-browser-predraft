package net.buildabrowser.babbrowser.input.test;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.input.FocusManagerContext;

public class TestFocusManagerContext implements FocusManagerContext {

  @Override
  public void onFocusChanged(Node oldFocused, Node newFocused) {}

  @Override
  public FocusIgnore getIgnore(Node node) {
    if (!(node instanceof Element element)) return FocusIgnore.NONE;
    return
      element.name().equals("ignore-self") ? FocusIgnore.SELF :
      element.name().equals("ignore-tree") ? FocusIgnore.TREE :
      FocusIgnore.NONE;
  }
  
}
