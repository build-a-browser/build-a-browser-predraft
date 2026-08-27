package net.buildabrowser.babbrowser.dom.listener;

import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public class ForkedDocumentChangeListener extends AbstractDocumentChangeListener {
 
  private final List<DocumentChangeListener> nextListeners;

  public ForkedDocumentChangeListener(
    DocumentChangeListener... nextListeners
  ) {
    super(null);
    this.nextListeners = List.of(nextListeners);
  }

  public ForkedDocumentChangeListener(
    DocumentChangeListener extraListener,
    List<DocumentChangeListener> nextListeners
  ) {
    super(extraListener);
    this.nextListeners = nextListeners;
  }

  @Override
  public void onNodeAdded(Node node) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onNodeAdded(node);
    }
    super.onNodeAdded(node);
  }

  @Override
  public void onNodeRemoved(Node node) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onNodeRemoved(node);
    }
    super.onNodeRemoved(node);
  }

  @Override
  public void onAttributeChanged(
    Element element, String attrName, String prevValue, String newValue
  ) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onAttributeChanged(element, attrName, prevValue, newValue);
    }
    super.onAttributeChanged(element, attrName, prevValue, newValue);
  }

  @Override
  public void onStylesheetAdded(CSSStyleSheet styleSheet) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onStylesheetAdded(styleSheet);
    }
    super.onStylesheetAdded(styleSheet);
  }

  @Override
  public boolean onElementEvent(Element element, Event event, boolean allowDefault) {
    for (DocumentChangeListener nextListener: nextListeners) {
      allowDefault = nextListener.onElementEvent(element, event, allowDefault);
    }

    return super.onElementEvent(element, event, allowDefault);
  }

  @Override
  public boolean onElementEventEarly(Element element, Event event, boolean allowDefault) {
    for (DocumentChangeListener nextListener: nextListeners) {
      allowDefault = nextListener.onElementEventEarly(element, event, allowDefault);
    }

    return super.onElementEventEarly(element, event, allowDefault);
  }

  @Override
  public void onSelectionChanged() {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onSelectionChanged();;
    }
    super.onSelectionChanged();
  }

  @Override
  public void onURLChanged(URI prevURL, URI newURL) {
    for (DocumentChangeListener nextListener: nextListeners) {
      nextListener.onURLChanged(prevURL, newURL);
    }
    super.onURLChanged(prevURL, newURL);
  }

  protected List<DocumentChangeListener> nextListeners() {
    return this.nextListeners;
  }

}
