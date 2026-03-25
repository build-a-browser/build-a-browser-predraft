package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.dom.Document;

public record DocumentState(
  Document document
) {
 
  public static DocumentState create(Document document) {
    return new DocumentState(document);
  }

}
