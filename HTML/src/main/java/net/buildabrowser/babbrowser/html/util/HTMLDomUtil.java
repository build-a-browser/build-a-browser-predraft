package net.buildabrowser.babbrowser.html.util;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.infra.Namespace;

public final class HTMLDomUtil {
  
  private HTMLDomUtil() {}

  public static boolean isHtmlElement(
    Node node, String type
  ) {
    return
      node instanceof HTMLElement element
      && element.namespace().equals(Namespace.HTML_NAMESPACE)
      && element.name().equals(type);
  }

}
