package net.buildabrowser.babbrowser.html.navigation.imp;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;

public class DocumentStateImp implements DocumentState {

  private RenderableDocument document;

  @Override
  public RenderableDocument document() {
    return this.document;
  }

  @Override
  public void setDocument(RenderableDocument document) {
    this.document = document;
  }
  
}
