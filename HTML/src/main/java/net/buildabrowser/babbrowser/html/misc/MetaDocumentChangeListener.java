package net.buildabrowser.babbrowser.html.misc;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;

public class MetaDocumentChangeListener extends AbstractDocumentChangeListener {

  public MetaDocumentChangeListener(
    DocumentChangeListener nextListener
  ) {
    super(nextListener);
  }

  public void onNodeAdded(Node node) {
    super.onNodeAdded(node);

    if (
      // TODO: Custom MetaElement tyype
      node instanceof HTMLElement element
      && element.name().equals("meta")
    ) {
      String httpEquiv = element.getAttribute("http-equiv");
      if (httpEquiv == null) return;
      switch (httpEquiv) {
        case "refresh" -> handleMetaRefresh(element);
        default -> {}
      }
    }
  }

  private void handleMetaRefresh(HTMLElement element) {
    String input = element.getAttribute("content");
    if (input == null || input.isEmpty()) return;
    
    SharedDeclarativeRefreshSteps.run((HTMLDocument) element.nodeDocument(), input);
  }

}
