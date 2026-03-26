package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;

public record DocumentState(
  HTMLDocument document
) {
 
  public static DocumentState create(HTMLDocument document) {
    return new DocumentState(document);
  }

}
