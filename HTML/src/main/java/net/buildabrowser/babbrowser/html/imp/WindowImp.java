package net.buildabrowser.babbrowser.html.imp;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.scripting.SimilarOriginWindowAgent;
import net.buildabrowser.babbrowser.html.scripting.Window;

public class WindowImp implements Window {

  private final SimilarOriginWindowAgent agent;
  private final HTMLDocument document;

  public WindowImp(
    SimilarOriginWindowAgent agent,
    HTMLDocument document
  ) {
    this.agent = agent;
    this.document = document;
  }

  @Override
  public SimilarOriginWindowAgent agent() {
    return this.agent;
  }

  @Override
  public HTMLDocument associatedDocument() {
    return this.document;
  }
  
}
