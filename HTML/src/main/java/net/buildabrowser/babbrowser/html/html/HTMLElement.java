package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.common.datastruct.Slottable;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLElementImp;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface HTMLElement extends Element, HTMLOrSVGOrMathMLElement, Slottable {

  String innerText();

  // Extensions

  Navigable nodeNavigable();

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }
  
}
