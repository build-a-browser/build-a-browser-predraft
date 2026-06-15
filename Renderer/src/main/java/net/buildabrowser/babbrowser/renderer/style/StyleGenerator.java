package net.buildabrowser.babbrowser.renderer.style;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public final class StyleGenerator {
 
  private StyleGenerator() {}

  public static void style(
    Node node,
    StyleCache styleCache,
    SlotFamily<HTMLElement, ElementContext> elementContexts,
    ElementSet changedElements
  ) {
    if (changedElements.isEmpty()) return;

    for (Element changedElement: changedElements) {
      if (elementHasNoChangedAncestors(changedElement, changedElements)) {
        style(changedElement, styleCache, elementContexts);
      }
    }
  }

  public static void style(
    Node node,
    StyleCache styleCache,
    SlotFamily<HTMLElement, ElementContext> elementContexts
  ) {
    if (node instanceof HTMLElement element) {
      elementContexts.get(element).regenerateStyles(styleCache);
    }

    Node childNode = node.firstChild();
    while (childNode != null) {
      style(childNode, styleCache, elementContexts);
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
