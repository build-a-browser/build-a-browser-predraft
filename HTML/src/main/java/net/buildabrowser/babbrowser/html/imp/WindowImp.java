package net.buildabrowser.babbrowser.html.imp;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.scripting.SimilarOriginWindowAgent;
import net.buildabrowser.babbrowser.html.scripting.Window;

public class WindowImp implements Window {

  private final SimilarOriginWindowAgent agent;
  private final Document document;

  public WindowImp(
    SimilarOriginWindowAgent agent,
    Document document
  ) {
    this.agent = agent;
    this.document = document;
  }

  @Override
  public SimilarOriginWindowAgent agent() {
    return this.agent;
  }

  @Override
  public Document associatedDocument() {
    return this.document;
  }
  
}
