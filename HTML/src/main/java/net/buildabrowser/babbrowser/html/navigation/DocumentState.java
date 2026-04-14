package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.DocumentStateImp;

public interface DocumentState {

  HTMLDocument document();

  void setDocument(HTMLDocument document);
 
  public static DocumentState create() {
    return new DocumentStateImp();
  }

}
