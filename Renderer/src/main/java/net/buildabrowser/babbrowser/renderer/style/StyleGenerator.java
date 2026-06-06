package net.buildabrowser.babbrowser.renderer.style;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.imp.ElementContextImp;

public final class StyleGenerator {
 
  private StyleGenerator() {}

  public static void style(Node node, StyleCache styleCache, ElementSet changedElements) {
    if (changedElements.isEmpty()) return;

    for (Element changedElement: changedElements) {
      if (elementHasNoChangedAncestors(changedElement, changedElements)) {
        style(changedElement, styleCache);
      }
    }
  }

  public static void style(Node node, StyleCache styleCache) {
    if (node instanceof HTMLElement element) {
      ((ElementContextImp) element.getContext()).regenerateStyles(styleCache);
    }

    Node childNode = node.firstChild();
    while (childNode != null) {
      style(childNode, styleCache);
      childNode = childNode.nextSibling();
    }
  }

  private static boolean elementHasNoChangedAncestors(Element changedElement, ElementSet changedElements) {
    Node parent = changedElement.parentNode();
    while (parent != null) {
      if (
        parent instanceof Element parentElement
        && changedElements.contains(parentElement)
      ) return false;
      parent = parent.parentNode();
    }

    return true;
  }

}
