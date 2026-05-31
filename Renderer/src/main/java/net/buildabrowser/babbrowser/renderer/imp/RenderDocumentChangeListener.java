package net.buildabrowser.babbrowser.renderer.imp;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class RenderDocumentChangeListener extends AbstractDocumentChangeListener {

  public RenderDocumentChangeListener(DocumentChangeListener innerListener) {
    super(innerListener);
  }

  @Override
  public void onNodeAdded(Node node) {
    if (node instanceof HTMLElement element) {
      element.setContext(ElementContext.create(element));
    }
    super.onNodeAdded(node);
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    super.onAttributeChanged(element, attrName, prevValue, newValue);
    if (
      element instanceof HTMLElement Element
      && Element.getContext() instanceof ElementContext elementContext
    ) {
      elementContext.onAttributeValueChanged(attrName, prevValue, newValue);
    }
  }

}
