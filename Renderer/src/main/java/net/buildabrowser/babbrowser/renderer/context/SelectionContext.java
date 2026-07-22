package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.renderer.context.imp.SelectionContextImp;

public interface SelectionContext {
  
  boolean selected(Node node);

  long selectionStart(Node node);

  long selectionEnd(Node node);

  void updateSelection();

  static SelectionContext create(
    Selection selection,
    ElementSet selectionSet
  ) {
    return new SelectionContextImp(selection, selectionSet);
  }

}
