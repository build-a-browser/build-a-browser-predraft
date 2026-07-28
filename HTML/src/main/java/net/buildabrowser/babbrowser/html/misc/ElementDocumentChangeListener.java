package net.buildabrowser.babbrowser.html.misc;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.html.LinkElement;
import net.buildabrowser.babbrowser.html.link.LinkProcessor;

public class ElementDocumentChangeListener extends AbstractDocumentChangeListener {
 
  private final FetchEngine fetchEngine;

  public ElementDocumentChangeListener(
    FetchEngine fetchEngine,
    DocumentChangeListener nextListener
  ) {
    super(nextListener);
    this.fetchEngine = fetchEngine;
  }

  public void onNodeAdded(Node node) {
    // TODO: Once more Element interfaces are defined, simplify this to a switch
    if (
      node instanceof LinkElement element
      && element.name().equals("link")
    ) {
      // TODO: Also more cases that trigger the linked resource
      LinkProcessor.processLink(element, fetchEngine);
    } else if (
      // TODO: Custom MetaElement tyype
      node instanceof HTMLElement element
      && element.name().equals("meta")
    ) {
      handleMeta(element);
    }

    if (node instanceof FormAssociatedElement formAssociatedElement) {
      // TODO: Check parser inserted flag
      formAssociatedElement.resetFormOwner();
    }

    super.onNodeAdded(node);
  }

  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    if (element instanceof HTMLInputElement inputElement) {
      handleInputElementAttributeChange(inputElement, attrName, prevValue, newValue);
    }
    super.onAttributeChanged(element, attrName, prevValue, newValue);
  }

  private void handleMeta(HTMLElement element) {
    String httpEquiv = element.getAttribute("http-equiv");
    if (httpEquiv == null) return;
    switch (httpEquiv) {
      case "refresh" -> handleMetaRefresh(element);
      default -> {}
    }
  }

  private void handleMetaRefresh(HTMLElement element) {
    String input = element.getAttribute("content");
    if (input == null || input.isEmpty()) return;
    
    SharedDeclarativeRefreshSteps.run((HTMLDocument) element.nodeDocument(), input);
  }

  private void handleInputElementAttributeChange(
    HTMLInputElement element,
    String attrName, String prevValue, String newValue
  ) {
    if (attrName.equals("value")) {
      // TODO: Need to check if old value was dirty
      element.setValue(newValue);
    } else if (attrName.equals("checked")) {
      // TODO: Check dirty checkedness
      element.setChecked(newValue != null);
    }
  }

}
