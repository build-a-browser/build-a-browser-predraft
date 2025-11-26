package net.buildabrowser.babbrowser.browser.render.imp;

import net.buildabrowser.babbrowser.browser.render.core.context.ElementContext;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class RenderDocumentChangeListener implements DocumentChangeListener {
  
  private final DocumentChangeListener styleDocumentChangeListener;

  public RenderDocumentChangeListener(DocumentChangeListener styleDocumentChangeListener) {
    this.styleDocumentChangeListener = styleDocumentChangeListener;
  }

  @Override
  public void onNodeAdded(Node node) {
    if (node instanceof MutableElement element) {
      element.setContext(ElementContext.create());
    }
    styleDocumentChangeListener.onNodeAdded(node);
  }

  @Override
  public void onNodeRemoved(Node node) {
    styleDocumentChangeListener.onNodeRemoved(node);
  }

}
