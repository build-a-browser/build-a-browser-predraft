package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.imp.DocumentStateImp;

public interface DocumentState {

  RenderableDocument document();

  void setDocument(RenderableDocument document);

  boolean reloadPending();

  void setReloadPending(boolean reloadPending);

  boolean everPopulated();

  void setEverPopulated(boolean everPopulated);

  // PostResource or String (I wish Java had union types)
  Object resource();

  void setResource(Object documentResource);
 
  public static DocumentState create() {
    return new DocumentStateImp();
  }

}
