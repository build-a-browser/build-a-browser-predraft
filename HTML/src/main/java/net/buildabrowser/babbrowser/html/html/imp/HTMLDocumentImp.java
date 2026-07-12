package net.buildabrowser.babbrowser.html.html.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.dom.imp.DocumentImp;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;

public class HTMLDocumentImp extends DocumentImp implements HTMLDocument {

  private final UANavigableOptions uaNavigableOptions;
  private final BrowsingContext browsingContext;
  private final FocusManager focusManager;

  private DocumentRenderer renderer;
  private boolean willDeclarativelyRefresh;
  private HTMLElement titleElement;

  public HTMLDocumentImp(
    UANavigableOptions uaNavigableOptions,
    BrowsingContext browsingContext
  ) {
    this.uaNavigableOptions = uaNavigableOptions;
    this.browsingContext = browsingContext;
    this.focusManager = FocusManager.create(this);
  }

  @Override
  public String title() {
    if (titleElement == null) return "";
    // TODO: Proper way to get title text
    if (titleElement.firstChild() instanceof Text text) {
      return text.data().trim();
    }

    return "";
  }

  @Override
  public void setTitleElement(HTMLElement titleElement) {
    this.titleElement = titleElement;
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
  public DocumentChangeListener changeListener() {
    if (renderer == null) {
      return super.changeListener();
    } else {
      return renderer.changeListener();
    }
  }

  @Override
  public FocusManager focusManager() {
    return this.focusManager;
  }

  @Override
  public UANavigableOptions uaNavigableOptions() {
    return this.uaNavigableOptions;
  }

  private void syncStylesheets(DocumentChangeListener changeListener) {
    for (CSSStyleSheet styleSheet: styleSheets()) {
      changeListener.onStylesheetAdded(styleSheet);
    }
  }

  private void syncNodes(DocumentChangeListener changeListener, Node node) {
    changeListener.onNodeAdded(this);
    node.forEachChild(childNode -> {
      syncNodes(changeListener, childNode);
    });
  }
  
}
