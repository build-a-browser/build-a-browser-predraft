package net.buildabrowser.babbrowser.html.selection;

import java.util.LinkedList;

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
      new LinkedList<>(),
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
    LinkedList<Node> enteredNodes,
    SelectionSearchState state,
    SelectionUtilCallbacks callbacks
  ) {
    boolean isScan = state.equals(SelectionSearchState.SCAN);
    if (isScan) {
      enteredNodes.push(currentNode);
    }
    if (isScan && currentNode == sourceNode) {
      state = SelectionSearchState.ACTIVE;
      for (int i = enteredNodes.size() - 1; i >= 0; i--) {
        callbacks.onNodeEntered(enteredNodes.get(i));
      }
    }

    if (state.equals(SelectionSearchState.ACTIVE)) {
      callbacks.onNodeSelected(currentNode);
    }

    Node innerNode = currentNode.firstChild();
    while (innerNode != null) {
      if (state.equals(SelectionSearchState.ACTIVE)) {
        callbacks.onNodeEntered(innerNode);
      }

      state = traverseForSelection(
        innerNode, sourceNode, targetNode,
        enteredNodes, state, callbacks);

      if (state.equals(SelectionSearchState.SCAN)) {
        enteredNodes.pop();
      } else if (
        state.equals(SelectionSearchState.ACTIVE)
        || state.equals(SelectionSearchState.EXIT)
      ) {
        callbacks.onNodeExited(innerNode);
      }

      if (
        state.equals(SelectionSearchState.EXIT)
      ) return state;
      
      innerNode = innerNode.nextSibling();
    }

    if (currentNode == targetNode) {
      return SelectionSearchState.EXIT;
    }

    return state;
  }

  public static Node determineCommonParent(Node sourceNode, Node targetNode) {
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

    default void onNodeEntered(Node node) {}

    default void onNodeExited(Node node) {}
    
  }

  private static enum SelectionSearchState {
    SCAN, ACTIVE, EXIT
  }

}
