package net.buildabrowser.babbrowser.html.html;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public interface RenderableDocument extends Invalidatable {
 
  String title();

  URI url();
  
  BrowsingContext browsingContext();

  DocumentRenderer renderer();

  void attachRenderer(DocumentRenderer renderer);

}
