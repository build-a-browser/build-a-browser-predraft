package net.buildabrowser.babbrowser.html.navigation.util;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.BrowsingContext;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.navigation.UANavigableOptions;

public final class TraversableUtil {
  
  private TraversableUtil() {}

  public static Navigable createNewTopLevelTraversable(
    UANavigableOptions uaNavigableOptions
  ) {
    HTMLDocument document = BrowsingContext.create(uaNavigableOptions).activeDocument();
    DocumentState documentState = DocumentState.create();
    documentState.setDocument(document);
    // TODO: Support aux context
    Navigable traversable = Navigable.create(uaNavigableOptions, documentState);
    // TODO: Some other stuff
    return traversable;
  }

}
