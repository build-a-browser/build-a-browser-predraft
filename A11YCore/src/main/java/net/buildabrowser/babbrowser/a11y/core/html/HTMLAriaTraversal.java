package net.buildabrowser.babbrowser.a11y.core.html;

import net.buildabrowser.babbrowser.a11y.core.AriaCallbacks;
import net.buildabrowser.babbrowser.a11y.core.AriaProperty;
import net.buildabrowser.babbrowser.a11y.core.AriaRole;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public final class HTMLAriaTraversal {
  
  private HTMLAriaTraversal() {}

  public static <T> void traverse(
    T parentNodeRepr,
    Node node,
    AriaCallbacks<T> callbacks
  ) {
    AriaRole role = HTMLRoleMapper.mapNode(node);
    if (isIgnored(node, role)) {
      node.forEachChild(child -> traverse(parentNodeRepr, child, callbacks));
      return;
    }

    T nodeRepr = callbacks.visitNode(parentNodeRepr, node.ariaId(), role);
    // TODO: Compute name and description
    // TODO: Some element types should have implicit attributes
    addNodeAttributes(nodeRepr, node, callbacks);
    node.forEachChild(child -> traverse(nodeRepr, child, callbacks));
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
    Node node, AriaRole role
  ) {
    // TODO: Also check if a generic can be ignored
    return role == null;
  }

}
