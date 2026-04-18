package net.buildabrowser.babbrowser.html.html.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.imp.DocumentImp;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public class HTMLDocumentImp extends DocumentImp implements HTMLDocument {

  private final BrowsingContext browsingContext;

  private DocumentRenderer renderer;
  private boolean willDeclarativelyRefresh;

  public HTMLDocumentImp(BrowsingContext browsingContext) {
    this.browsingContext = browsingContext;
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
  public void attachRenderer(DocumentRenderer renderer) {
    // TODO: Might be good for Renderer to be an intrusive list in the future
    // (say we need a web renderer and PDF renderer)
    this.renderer = renderer;

    syncStylesheets(renderer.changeListener());
    syncNodes(renderer.changeListener(), this);
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
  public Navigable nodeNavigable() {
    return browsingContext().activeWindow().agent().eventLoop().getNavigable(this);
  }

  @Override
  public boolean willDeclarativelyRefresh() {
    return this.willDeclarativelyRefresh;
  }

  @Override
  public void setWillDeclarativelyRefresh(boolean willDeclarativelyRefresh) {
    this.willDeclarativelyRefresh = willDeclarativelyRefresh;
  }

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    if (renderer != null) {
      renderer.onDocumentInvalidated(invalidationLevel);
    }
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

  @Override
  public DocumentChangeListener changeListener() {
    if (renderer == null) {
      return super.changeListener();
    } else {
      return renderer.changeListener();
    }
  }

  private void syncStylesheets(DocumentChangeListener changeListener) {
    for (CSSStyleSheet styleSheet: styleSheets()) {
      changeListener.onStylesheetAdded(styleSheet);
    }
  }

  private void syncNodes(DocumentChangeListener changeListener, Node node) {
    changeListener.onNodeAdded(this);
    for (Node childNode: node.childNodes()) {
      syncNodes(changeListener, childNode);
    }
  }
  
}
