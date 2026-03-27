package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.cssbase.cssom.LinkStyle;
import net.buildabrowser.babbrowser.dom.Namespace;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.html.html.imp.LinkElementImp;

public interface LinkElement extends MutableElement, LinkStyle {
 
  public static LinkElement create(
    String name, MutableNode parentNode
  ) {
    return new LinkElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
