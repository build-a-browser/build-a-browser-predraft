package net.buildabrowser.babbrowser.render.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.render.context.ElementContext;

public class RenderDocumentChangeListener implements DocumentChangeListener {
  
  private final DocumentChangeListener styleDocumentChangeListener;

  public RenderDocumentChangeListener(DocumentChangeListener styleDocumentChangeListener) {
    this.styleDocumentChangeListener = styleDocumentChangeListener;
  }

  @Override
  public void onNodeAdded(Node node) {
    if (node instanceof MutableElement element) {
      element.setContext(ElementContext.create(element));
    }
    styleDocumentChangeListener.onNodeAdded(node);
  }

  @Override
  public void onNodeRemoved(Node node) {
    styleDocumentChangeListener.onNodeRemoved(node);
  }

  @Override
  public void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {
    styleDocumentChangeListener.onAttributeChanged(element, attrName, prevValue, newValue);
    if (
      element instanceof MutableElement mutableElement
      && mutableElement.getContext() instanceof ElementContext elementContext
    ) {
      elementContext.onAttributeValueChanged(attrName, prevValue, newValue);
    }
  }

  @Override
  public void onStylesheetAdded(CSSStyleSheet styleSheet) {
    styleDocumentChangeListener.onStylesheetAdded(styleSheet);
  };

}
