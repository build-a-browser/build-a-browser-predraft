package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLButtonElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLButtonElement extends FormAssociatedElement {

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLButtonElementImp(
      name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
