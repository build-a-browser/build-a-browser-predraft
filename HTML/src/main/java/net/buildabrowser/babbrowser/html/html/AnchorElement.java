package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.AnchorElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface AnchorElement extends HTMLElement {

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new AnchorElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }
  
}
