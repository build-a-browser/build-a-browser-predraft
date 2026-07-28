package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.SubmittableElementSet;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;

public class HTMLFormElementImp extends HTMLElementImp implements HTMLFormElement {

  private final SubmittableElementSet submittableElements = SubmittableElementSet.create();

  private boolean constructEntryList = false;

  public HTMLFormElementImp(
    String name, String namespace, Node parentNode
  ) {
    super(name, namespace, parentNode);
  }

  @Override
  public boolean constructingEntryList() {
    return this.constructEntryList;
  }

  @Override
  public void setConstructingEntryList(boolean constructingEntryList) {
    this.constructEntryList = constructingEntryList;
  }

  @Override
  public SubmittableElementSet submittableElements() {
    return this.submittableElements;
  }
  
}
