package net.buildabrowser.babbrowser.html.navigation.imp;

import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.navigation.DocumentState;

public class DocumentStateImp implements DocumentState {

  private RenderableDocument document;
  private boolean reloadPending;
  private boolean everPopulated;

  @Override
  public RenderableDocument document() {
    return this.document;
  }

  @Override
  public void setDocument(RenderableDocument document) {
    this.document = document;
  }

  @Override
  public boolean reloadPending() {
    return this.reloadPending;
  }

  @Override
  public void setReloadPending(boolean reloadPending) {
    this.reloadPending = reloadPending;
  }

  @Override
  public boolean everPopulated() {
    return this.everPopulated;
  }

  @Override
  public void setEverPopulated(boolean everPopulated) {
    this.everPopulated = everPopulated;
  }
  
}
