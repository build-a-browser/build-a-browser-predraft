package net.buildabrowser.babbrowser.dom.listener;

import java.net.URI;

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
  public boolean onElementEvent(
    Element element, Event event, boolean allowDefault
  ) {
    if (nextListener == null) return allowDefault;
    return nextListener.onElementEvent(element, event, allowDefault);
  }

  @Override
  public boolean onElementEventEarly(
    Element element, Event event, boolean allowDefault
  ) {
    if (nextListener == null) return allowDefault;
    return nextListener.onElementEventEarly(element, event, allowDefault);
  }
  
  @Override
  public void onSelectionChanged() {
    if (nextListener == null) return;
    nextListener.onSelectionChanged();
  }

  @Override
  public void onURLChanged(URI prevURL, URI newURL) {
    if (nextListener == null) return;
    nextListener.onURLChanged(prevURL, newURL);
  }

  protected DocumentChangeListener nextListener() {
    return this.nextListener;
  }

}
