package net.buildabrowser.babbrowser.debugger.swing;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugFragment;
import net.buildabrowser.babbrowser.debugger.core.DebugObject;
import net.buildabrowser.babbrowser.debugger.core.DebugObject.DebugObjectSelection;
import net.buildabrowser.babbrowser.dom.Node;

public class SwingNodeSelection {

  private final SwingFrameDebugger frameDebugger;
  
  private DebugObject lastDebugObject;
  private boolean clickToSelectEnabled = false;

  public SwingNodeSelection(SwingFrameDebugger frameDebugger) {
    this.frameDebugger = frameDebugger;
  }
  
  public boolean clickToSelectEnabled() {
    return this.clickToSelectEnabled;
  }

  public void toggleClickToSelectEnabled() {
    this.clickToSelectEnabled = !this.clickToSelectEnabled;
    if (
      !this.clickToSelectEnabled
      && this.lastDebugObject != null
    ) {
      lastDebugObject.markSelection(DebugObjectSelection.NONE);
      lastDebugObject = null;
    }
  }

  public void select(
    DebugObject debugObject,
    DebugObjectSelection selection,
    boolean cancelClickToSelect
  ) {
    if (lastDebugObject != null) {
      lastDebugObject.markSelection(DebugObjectSelection.NONE);
    }
    if (cancelClickToSelect) {
      this.clickToSelectEnabled = false;
    }

    this.lastDebugObject = debugObject;
    if (debugObject == null) return;

    debugObject.markSelection(selection);
  }

  public void expand(Node node, DebugBox box, DebugFragment fragment) {
    frameDebugger.later(_ -> {
      LazyDiffTree<Node> nodeTree =
        node == null ? null : expandNodeTree(node);
      if (nodeTree != null) {
        nodeTree.select();
      }

      LazyDiffTree<DebugBox> boxTree =
        box == null ? null : expandBoxTree(box);
      if (boxTree != null) {
        boxTree.select();
      }
    });  
  }

  private LazyDiffTree<Node> expandNodeTree(Node node) {
    LazyDiffTree<Node> parentTree = node.parentNode() != null ?
      expandNodeTree(node.parentNode()) : null;
    LazyDiffTree<Node> tree = node.parentNode() != null ?
      (parentTree == null ? null : parentTree.child(node)) :
      frameDebugger.nodeTree();
    if (tree == null) return null;
    if (!(tree.object() == node)) return null;
    tree.openNow();
    return tree;
  }

  private LazyDiffTree<DebugBox> expandBoxTree(DebugBox box) {
    LazyDiffTree<DebugBox> parentTree = box.parentBox() != null ?
      expandBoxTree(box.parentBox()) : null;
    LazyDiffTree<DebugBox> tree = box.parentBox() != null ?
      (parentTree == null ? null : parentTree.child(box)) :
      frameDebugger.boxTree();
    if (tree == null) return null;
    if (!(tree.object() == box)) return null;
    tree.openNow();
    return tree;
  }
  
}
