package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.DocumentStateImp;

public interface DocumentState {

  RenderableDocument document();

  void setDocument(RenderableDocument document);
 
  public static DocumentState create() {
    return new DocumentStateImp();
  }

}
