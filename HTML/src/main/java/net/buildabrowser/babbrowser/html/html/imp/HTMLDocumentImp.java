package net.buildabrowser.babbrowser.html.html.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.dom.imp.DocumentImp;
import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.UAHTMLDocumentOptions;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public class HTMLDocumentImp extends DocumentImp implements HTMLDocument {

  private final BrowsingContext browsingContext;
  private final DocumentRenderer renderer;

  public HTMLDocumentImp(
    BrowsingContext browsingContext,
    UAHTMLDocumentOptions documentOptions
  ) {
    super(documentOptions.changeListener());
    this.browsingContext = browsingContext;
    this.renderer = documentOptions.renderer();
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
  
}
