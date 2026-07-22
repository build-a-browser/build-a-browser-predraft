package net.buildabrowser.babbrowser.renderer.context.imp;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.selection.Selection;
import net.buildabrowser.babbrowser.html.selection.Selection.SelectionDirection;
import net.buildabrowser.babbrowser.html.selection.SelectionUtil;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;

public class SelectionContextImp implements SelectionContext {

  private final Selection selection;
  private final ElementSet selectedElements;
  private final ElementSet selectionParents;

  public SelectionContextImp(
    Selection selection,
    ElementSet selectionSet
  ) {
    this.selection = selection;
    this.selectedElements = selectionSet;
    this.selectionParents = selectedElements.root().createChild();
  }

  @Override
  public boolean selected(Node node) {
    // TODO: Unlikely, but what if the node or parent is a Document?
    if (
      node instanceof Element element
      && selectedElements.contains(element)
    ) return true;

    if (!(
      node instanceof Text text
      && text.parentNode() instanceof Element element
      && selectionParents.contains(element)
    )) return false;

    boolean isBackwards = selection.direction().equals(SelectionDirection.BACKWARD);
    Node sourceNode = isBackwards ?
      selection.focusNode() :
      selection.anchorNode();
    Node targetNode = isBackwards ?
      selection.anchorNode() :
      selection.focusNode();
    Node adjustedSourceNode = SelectionUtil.ancestorWithParent(sourceNode, node.parentNode());
    Node adjustedTargetNode = SelectionUtil.ancestorWithParent(targetNode, node.parentNode());
    boolean parentHasSource = adjustedSourceNode != null;
    boolean parentHasTarget = adjustedTargetNode != null;
    if (!(parentHasSource || parentHasTarget)) {
      return true;
    }

    Node currentNode = parentHasSource ?
      adjustedSourceNode : node.parentNode().firstChild();
    while (currentNode != null) {
      if (currentNode == node) return true;
      if (currentNode == adjustedTargetNode) return false;
      currentNode = currentNode.nextSibling();
    }

    return false;
  }

  @Override
  public long selectionStart(Node node) {
    boolean isBackwards = selection.direction().equals(SelectionDirection.BACKWARD);

    if (
      !isBackwards
      && node == selection.anchorNode()
    ) return selection.anchorOffset();

    if (
      isBackwards
      && node == selection.focusNode()
    ) return selection.focusOffset();

    return 0;
  }

  @Override
  public long selectionEnd(Node node) {
    boolean isBackwards = selection.direction().equals(SelectionDirection.BACKWARD);

    if (
      !isBackwards
      && node == selection.focusNode()
    ) return selection.focusOffset();

    if (
      isBackwards
      && node == selection.anchorNode()
    ) return selection.anchorOffset();

    if (node instanceof Text text) {
      return text.data().length();
    }

    // TODO: Return length of node
    return 0;
  }

  @Override
  public void updateSelection() {
    selectedElements.removeAll();
    selectionParents.removeAll();
    SelectionUtil.determineSelectedNodes(
      selection,
      node -> {
        if (node instanceof Element element) {
          selectedElements.add(element);
        } else if (
          node instanceof Text text
          && text.parentNode() instanceof Element element
        ) {
          selectionParents.add(element);
        }
      });
  }
  
}
