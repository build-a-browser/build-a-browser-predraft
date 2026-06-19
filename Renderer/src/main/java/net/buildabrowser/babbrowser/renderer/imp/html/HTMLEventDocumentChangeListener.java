package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.input.FocusOptions;

public class HTMLEventDocumentChangeListener extends AbstractDocumentChangeListener {

  private final FocusManager focusManager;

  public HTMLEventDocumentChangeListener(
    HTMLDocument document,
    DocumentChangeListener nextListener
  ) {
    super(nextListener);
    this.focusManager = document.focusManager();
  }
  
  @Override
  public void onElementEvent(Element element, Event event) {
    if (event.type().equals("click")) {
      Element targetedElement = findFocusableElement(element);
      FocusOptions focusOptions = new FocusOptions();
      focusManager.focus(targetedElement, focusOptions);
    }
    super.onElementEvent(element, event);
  }

  private Element findFocusableElement(Node currentTarget) {
    while (currentTarget != null) {
      if (
        currentTarget instanceof HTMLElement element
        && element.tabIndex() >= 0
      ) return element;
      currentTarget = currentTarget.parentNode();
    }

    return null;
  }

}
