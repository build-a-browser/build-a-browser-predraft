package net.buildabrowser.babbrowser.render.style;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.context.imp.ElementContextImp;

public final class StyleGenerator {
 
  private StyleGenerator() {}

  public static void style(Node node) {
    if (node instanceof HTMLElement element) {
      ((ElementContextImp) element.getContext()).regenerateStyles();
    }

    Node childNode = node.firstChild();
    while (childNode != null) {
      style(childNode);
      childNode = childNode.nextSibling();
    }
  }

}
