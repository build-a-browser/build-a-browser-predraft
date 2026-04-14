package net.buildabrowser.babbrowser.dom.listener;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public abstract class AbstractDocumentChangeListener implements DocumentChangeListener {
 
  private final DocumentChangeListener nextListener;

  public AbstractDocumentChangeListener(DocumentChangeListener nextListener) {
    this.nextListener = nextListener;
  }

  public void onNodeAdded(Node node) {
    if (nextListener == null) return;
    nextListener.onNodeAdded(node);
  }

  public void onNodeRemoved(Node node) {
    if (nextListener == null) return;
    nextListener.onNodeRemoved(node);
  }

  public void onAttributeChanged(
    Element element, String attrName, String prevValue, String newValue
  ) {
    if (nextListener == null) return;
    nextListener.onAttributeChanged(element, attrName, prevValue, newValue);
  }

  public void onStylesheetAdded(CSSStyleSheet styleSheet) {
    if (nextListener == null) return;
    nextListener.onStylesheetAdded(styleSheet);
  }

}
