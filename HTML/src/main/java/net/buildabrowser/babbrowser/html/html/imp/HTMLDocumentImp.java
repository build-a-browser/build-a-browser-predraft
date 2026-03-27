package net.buildabrowser.babbrowser.html.html.imp;

import java.net.URI;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.imp.DocumentImp;
import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.UAHTMLDocumentOptions;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public class HTMLDocumentImp extends DocumentImp implements HTMLDocument {

  private final BrowsingContext browsingContext;
  private final DocumentRenderer renderer;
  private final Consumer<InvalidationLevel> onInvalidate;

  public HTMLDocumentImp(
    BrowsingContext browsingContext,
    UAHTMLDocumentOptions documentOptions
  ) {
    super(documentOptions.changeListener());
    this.browsingContext = browsingContext;
    this.renderer = documentOptions.renderer();
    this.onInvalidate = documentOptions.onInvalidate();
  }

  @Override
  public Node appendChild(Node node) {
    super.appendChild(node);

    if (node instanceof Invalidatable invalidatable) {
      invalidatable.invalidate(InvalidationLevel.BOX);
    }
    invalidate(InvalidationLevel.BOX);

    return node;
  }

  @Override
  public BrowsingContext browsingContext() {
    return this.browsingContext;
  }

  @Override
  public DocumentRenderer renderer() {
    return this.renderer;
  }

  @Override
  public URI fallbackURL() {
    // TODO: Implement
    return url();
  }

  @Override
  public URI baseURL() {
    // TODO: Implement
    return fallbackURL();
  }

  @Override
  public FetchClient relevantSettingsObject() {
    return browsingContext.realm().hostDefined();
  }

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    onInvalidate.accept(invalidationLevel);
  }

  @Override
  public void validate() {
    Node currentNode = firstChild();
    while (currentNode != null) {
      if (currentNode instanceof Invalidatable invalidatable) {
        invalidatable.validate();
      }
      currentNode = currentNode.nextSibling();
    }
  }
  
}
