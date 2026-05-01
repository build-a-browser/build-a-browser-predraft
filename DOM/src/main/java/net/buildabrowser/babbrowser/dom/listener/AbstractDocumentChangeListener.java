package net.buildabrowser.babbrowser.dom.listener;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public abstract class AbstractDocumentChangeListener implements DocumentChangeListener {
 
  private final DocumentChangeListener nextListener;

  public AbstractDocumentChangeListener(DocumentChangeListener nextListener) {
    this.nextListener = nextListener;
  }

  @Override
  public void onNodeAdded(Node node) {
    if (nextListener == null) return;
    nextListener.onNodeAdded(node);
  }

  @Override
  public void onNodeRemoved(Node node) {
    if (nextListener == null) return;
    nextListener.onNodeRemoved(node);
  }

  @Override
  public void onAttributeChanged(
    Element element, String attrName, String prevValue, String newValue
  ) {
    if (nextListener == null) return;
    nextListener.onAttributeChanged(element, attrName, prevValue, newValue);
  }

  @Override
  public void onStylesheetAdded(CSSStyleSheet styleSheet) {
    if (nextListener == null) return;
    nextListener.onStylesheetAdded(styleSheet);
  }

  @Override
  public void onElementEvent(Element element, Event event) {
    if (nextListener == null) return;
    nextListener.onElementEvent(element, event);
  }

}
