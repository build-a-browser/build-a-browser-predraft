package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.renderer.box.imp.DocumentBoxImp;

public interface DocumentBox extends Box {
  
  ElementBox htmlBox();

  void setChild(ElementBox child);

  HTMLDocument document();

  static DocumentBox create(HTMLDocument document) {
    return new DocumentBoxImp(document);
  }

}
