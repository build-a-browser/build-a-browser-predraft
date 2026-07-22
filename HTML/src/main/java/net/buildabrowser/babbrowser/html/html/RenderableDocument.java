package net.buildabrowser.babbrowser.html.html;

import java.net.URI;

import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public interface RenderableDocument {
 
  String title();

  URI url();
  
  BrowsingContext browsingContext();

  DocumentRenderer renderer();

  Navigable nodeNavigable();

  void attachRenderer(DocumentRenderer renderer);

}
