package net.buildabrowser.babbrowser.html.link;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.html.LinkElement;

// TODO: Also more cases that trigger the linked resource
public class LinkDocumentChangeListener extends AbstractDocumentChangeListener {
 
  private final FetchEngine fetchEngine;

    public LinkDocumentChangeListener(
    FetchEngine fetchEngine,
    DocumentChangeListener nextListener
  ) {
    super(nextListener);
    this.fetchEngine = fetchEngine;
  }

  public void onNodeAdded(Node node) {
    if (
      node instanceof LinkElement element
      && element.name().equals("link")
    ) {
      LinkProcessor.processLink(element, fetchEngine);
    }
    super.onNodeAdded(node);
  }

}
