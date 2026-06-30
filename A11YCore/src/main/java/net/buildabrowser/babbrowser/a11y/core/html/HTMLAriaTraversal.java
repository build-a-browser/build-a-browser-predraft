package net.buildabrowser.babbrowser.a11y.core.html;

import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.a11y.core.AriaCallbacks;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaProperty;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;

public final class HTMLAriaTraversal {
  
  private HTMLAriaTraversal() {}

  public static <T> void traverse(
    T parentNodeRepr,
    Node node,
    AriaCallbacks<T> callbacks,
    A11YOps ops
  ) {
    AriaRole role = HTMLRoleMapper.mapNode(node);
    if (ops.isSkipped(node)) {
      return;
    } else if (
      isIgnored(node, role, ops)
      || ops.isIgnored(node)
    ) {
      node.forEachChild(child -> traverse(parentNodeRepr, child, callbacks, ops));
      return;
    }

    T nodeRepr = callbacks.visitNode(parentNodeRepr, node.ariaId(), role);
    if (node instanceof Text text) {
      callbacks.visitText(nodeRepr, text.data().trim());
    }
    // TODO: Compute name and description
    // TODO: Some element types should have implicit attributes
    addNodeAttributes(nodeRepr, node, callbacks);
    node.forEachChild(child -> traverse(nodeRepr, child, callbacks, ops));
    callbacks.exitNode(nodeRepr, node.ariaId());
  }

  private static <T> void addNodeAttributes(T nodeRepr, Node node, AriaCallbacks<T> callbacks) {
    if (!(node instanceof Element element)) return;
    
    for (String attributeName: element.getAttributeNames()) {
      AriaProperty relatedProperty = AriaProperty.lookup(attributeName);
      if (relatedProperty == null) continue;
      String attributeValue = element.getAttribute(attributeName);
      callbacks.visitAttribute(nodeRepr, relatedProperty, attributeValue);
    }
  }

  private static boolean isIgnored(
    Node node, AriaRole role, A11YOps ops
  ) {
    // TODO: Also check if a generic can be ignored
    boolean simpleIgnore =
      role == null
      || (node instanceof Text text && text.data().isBlank());  
    if (simpleIgnore) return true;

    if (!(
      node instanceof Element element
      && (role.equals(AriaRole.GENERIC) || role.equals(AriaRole.PRESENTATION))
    )) return false;

    if (
      element.name().equals("html")
      || element.name().equals("body")
      || element.hasAttribute("id")
      || element.hasAttribute("role")
      || ops.hasSemanticMeaning(element)
    ) return false;

    for (String attrName: element.getAttributeNames()) {
      if (AriaProperty.lookup(attrName) != null) return false;
    }

    return true;
  }

}
