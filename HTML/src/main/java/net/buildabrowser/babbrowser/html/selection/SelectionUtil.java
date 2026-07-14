package net.buildabrowser.babbrowser.html.selection;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.selection.Selection.SelectionDirection;

public final class SelectionUtil {

  private SelectionUtil() {}

  public static SelectionDirection determineSelectionDirection(
    Selection selection
  ) {
    if (
      selection.anchorNode() == null
      || selection.focusNode() == null
    ) return SelectionDirection.FORWARD;

    Node anchorNode = selection.anchorNode();
    Node focusNode = selection.focusNode();
    if (anchorNode == focusNode) {
      return selection.focusOffset() < selection.anchorOffset() ?
        SelectionDirection.BACKWARD :
        SelectionDirection.FORWARD;
    }

    Node commonParent = determineCommonParent(anchorNode, focusNode);
    Node adjustedAnchorNode = ancestorWithParent(anchorNode, commonParent);
    Node adjustedFocusNode = ancestorWithParent(focusNode, commonParent);

    if (
      commonParent == null
      || adjustedAnchorNode == null
      || adjustedFocusNode == null
    ) return SelectionDirection.FORWARD;

    Node currentNode = commonParent.firstChild();
    while (currentNode != null) {
      if (currentNode == adjustedAnchorNode) {
        return SelectionDirection.FORWARD;
      } else if (currentNode == adjustedFocusNode) {
        return SelectionDirection.BACKWARD;
      }
      currentNode = currentNode.nextSibling();
    }

    return SelectionDirection.FORWARD;
  }

  // TODO: Ignore items that are not displayed?
  public static void determineSelectedNodes(
    Selection selection,
    SelectionUtilCallbacks callbacks
  ) {
    if (
      selection.anchorNode() == null
      || selection.focusNode() == null
    ) return;

    boolean isBackwards = selection.direction().equals(SelectionDirection.BACKWARD);
    Node sourceNode = isBackwards ?
      selection.focusNode() :
      selection.anchorNode();
    Node targetNode = isBackwards ?
      selection.anchorNode() :
      selection.focusNode();
    Node commonParent = determineCommonParent(sourceNode, targetNode);
    traverseForSelection(
      commonParent, sourceNode, targetNode,
      SelectionSearchState.SCAN, callbacks);
  }

  public static Node ancestorWithParent(Node currentNode, Node parentNode) {
    while (
      currentNode != null
      && currentNode.parentNode() != parentNode
    ) {
      currentNode = currentNode.parentNode();
    }

    return currentNode;
  }

  private static SelectionSearchState traverseForSelection(
    Node currentNode,
    Node sourceNode,
    Node targetNode,
    SelectionSearchState state,
    SelectionUtilCallbacks callbacks
  ) {
    boolean wasStartIncluded = state.equals(SelectionSearchState.ACTIVE);
    if (
      state.equals(SelectionSearchState.SCAN)
      && currentNode == sourceNode
    ) {
      state = SelectionSearchState.ACTIVE;
      // TODO: This is inconsistently called preorder or postorder
      callbacks.onNodeSelected(currentNode);
    }

    Node innerNode = currentNode.firstChild();
    while (innerNode != null) {
      state = traverseForSelection(
        innerNode, sourceNode, targetNode,
        state, callbacks);
      if (
        state.equals(SelectionSearchState.EXIT)
      ) return state;
      innerNode = innerNode.nextSibling();
    }

    boolean isTargetButNotSource = 
      currentNode == targetNode
      && currentNode != sourceNode;
    boolean isFullyIncluded =
      wasStartIncluded
      && state.equals(SelectionSearchState.ACTIVE);

    if (isTargetButNotSource || isFullyIncluded) {
      callbacks.onNodeSelected(currentNode);
    }

    if (currentNode == targetNode) {
      return SelectionSearchState.EXIT;
    }

    return state;
  }

  private static Node determineCommonParent(Node sourceNode, Node targetNode) {
    int sourceDepth = nodeDepth(sourceNode);
    int targetDepth = nodeDepth(targetNode);
    if (sourceDepth > targetDepth) {
      sourceNode = parents(sourceNode, sourceDepth - targetDepth);
    } else {
      targetNode = parents(targetNode, targetDepth - sourceDepth);
    }

    while (
      sourceNode != targetNode
    ) {
      sourceNode = sourceNode.parentNode();
      targetNode = targetNode.parentNode();
    }

    assert sourceNode != null;
    return sourceNode;
  }

  private static int nodeDepth(Node node) {
    int depth = -1;
    while (node != null) {
      depth++;
      node = node.parentNode();
    }
    return depth;
  }

  private static Node parents(Node node, int num) {
    for (int i = 0; i < num; i++) {
      node = node.parentNode();
    }

    return node;
  }

  public static interface SelectionUtilCallbacks {
  
    void onNodeSelected(Node node);
    
  }

  private static enum SelectionSearchState {
    SCAN, ACTIVE, EXIT
  }

}
