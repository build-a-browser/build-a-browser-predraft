package net.buildabrowser.babbrowser.html.scripting;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.imp.WindowImp;

public interface Window extends GlobalObject {

  SimilarOriginWindowAgent agent();
  
  Document associatedDocument();

  static Window create(
    SimilarOriginWindowAgent agent,
    Document document
  ) {
    return new WindowImp(agent, document);
  }

}
