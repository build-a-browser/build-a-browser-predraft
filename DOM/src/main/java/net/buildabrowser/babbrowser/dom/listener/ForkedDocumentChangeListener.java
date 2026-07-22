package net.buildabrowser.babbrowser.dom.listener;

import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public class ForkedDocumentChangeListener implements DocumentChangeListener {
 
  private final List<DocumentChangeListener> nextListeners;

  public ForkedDocumentChangeListener(
    DocumentChangeListener... nextListeners
  ) {
    this.nextListeners = List.of(nextListeners);
  }

  @Override
  public void onNodeAdded(Node node) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onNodeAdded(node);
    }
  }

  @Override
  public void onNodeRemoved(Node node) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onNodeRemoved(node);
    }
  }

  @Override
  public void onAttributeChanged(
    Element element, String attrName, String prevValue, String newValue
  ) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onAttributeChanged(element, attrName, prevValue, newValue);
    }
  }

  @Override
  public void onStylesheetAdded(CSSStyleSheet styleSheet) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onStylesheetAdded(styleSheet);
    }
  }

  @Override
  public boolean onElementEvent(Element element, Event event, boolean allowDefault) {
    for (DocumentChangeListener nextListener: nextListeners) {
      allowDefault = nextListener.onElementEvent(element, event, allowDefault);
    }

    return allowDefault;
  }

  @Override
  public void onSelectionChanged() {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onSelectionChanged();;
    }
  }

  @Override
  public void onURLChanged(URI prevURL, URI newURL) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onURLChanged(prevURL, newURL);
    }
  }

}
