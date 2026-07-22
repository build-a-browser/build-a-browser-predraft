package net.buildabrowser.babbrowser.html.html;

import java.util.List;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLFormElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLFormElement extends HTMLElement {
  
  // Extensions

  boolean constructingEntryList();

  void setConstructingEntryList(boolean constructingEntryList);

  List<FormAssociatedElement> submittableElements();

  void addSubmittableElement(FormAssociatedElement element);

  void removeSubmittableElement(FormAssociatedElement element);

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLFormElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
