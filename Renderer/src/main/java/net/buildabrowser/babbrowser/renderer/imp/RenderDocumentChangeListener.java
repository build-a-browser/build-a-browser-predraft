package net.buildabrowser.babbrowser.renderer.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public class RenderDocumentChangeListener extends AbstractDocumentChangeListener {

  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public RenderDocumentChangeListener(
    DocumentChangeListener innerListener,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    super(innerListener);
    this.renderContexts = renderContexts;
  }

  @Override
  public void onNodeAdded(Node node) {
    if (node instanceof HTMLElement element) {
      // Force creation now, so SlotItem#getExistingById does not return null
      renderContexts.get(element);
    }
    super.onNodeAdded(node);
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    super.onAttributeChanged(element, attrName, prevValue, newValue);
    if (element instanceof HTMLElement htmlElement) {
      RenderContext renderContext = renderContexts.get(htmlElement);
      if (renderContext instanceof ElementContext elementContext) {
        elementContext.onAttributeValueChanged(attrName, prevValue, newValue);
      }
    }
  }

}
