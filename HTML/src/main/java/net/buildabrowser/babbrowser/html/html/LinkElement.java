package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.cssbase.cssom.LinkStyle;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.LinkElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface LinkElement extends Element, LinkStyle {
 
  public static LinkElement create(
    String name, Node parentNode
  ) {
    return new LinkElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
