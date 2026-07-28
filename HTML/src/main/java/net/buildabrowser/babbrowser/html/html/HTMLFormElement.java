package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLFormElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLFormElement extends HTMLElement {
  
  // Extensions

  boolean constructingEntryList();

  void setConstructingEntryList(boolean constructingEntryList);

  SubmittableElementSet submittableElements();

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLFormElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
