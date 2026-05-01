package net.buildabrowser.babbrowser.html.navigation.imp;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;

public class DocumentStateImp implements DocumentState {

  private HTMLDocument document;

  @Override
  public HTMLDocument document() {
    return this.document;
  }

  @Override
  public void setDocument(HTMLDocument document) {
    this.document = document;
  }
  
}
