package net.buildabrowser.babbrowser.debugger.swing.gui;

import static javax.swing.SwingUtilities.invokeLater;

import java.util.function.Consumer;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree;
import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree.LazyDiffTreeListener;

public class JLazyDiffTreeListener<T> implements LazyDiffTreeListener<T> {

  private final JTree tree;
  private final DefaultMutableTreeNode treeNode;
  private final LazyDiffTree<T> innerTree;
  private final Consumer<T> onItemSelection;

  private String name = "";

  public JLazyDiffTreeListener(
    JTree tree,
    DefaultMutableTreeNode treeNode,
    LazyDiffTree<T> subtree,
    Consumer<T> onItemSelection
  ) {
    this.tree = tree;
    this.treeNode = treeNode;
    this.innerTree = subtree;
    this.onItemSelection = onItemSelection;
    this.name = subtree.name();
    treeNode.setUserObject(JLazyDiffTreeListener.this);

    if (subtree.isOpen()) {
      onOpened();
    }
  }

  @Override
  public void onNameChanged(String newName) {
    invokeLater(() -> {
      this.name = newName;
      ((DefaultTreeModel) tree.getModel()).nodeChanged(treeNode);
    });
  }

  @Override
  public void onOpening() {
    this.name += " (Loading...)";
    invokeLater(() -> {
      ((DefaultTreeModel) tree.getModel()).nodeChanged(treeNode);
    });
  }

  @Override
  public void onOpened() {
    this.name = innerTree.name();
    invokeLater(() -> {
      removePlaceholderNode();
      tree.expandPath(new TreePath(treeNode.getPath()));
      ((DefaultTreeModel) tree.getModel()).nodeChanged(treeNode);
    });
  }

  @Override
  public void onClosed(boolean isUISource) {
    invokeLater(() -> {
      treeNode.removeAllChildren();
      JLazyDiffTree.prepareTreeNodeForChildren(treeNode, innerTree);

      if (!isUISource) {
        // TODO: nodeStructureChanged expands the tree, but we can't call collapsePath again
        // if the source is the UI, as it would cause recursion. May lead to UI bugs
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.nodeStructureChanged(treeNode);
        TreePath nodePath = new TreePath(treeNode.getPath());
        tree.collapsePath(nodePath);
      }
    });
  }

  @Override
  public void onSelect() {
    invokeLater(() -> {
      TreePath path = new TreePath(treeNode.getPath());
      tree.setSelectionPath(path);
      tree.scrollPathToVisible(path);
    });
    onItemSelection.accept(innerTree.object());
  }

  // TODO: Batch add/remove ops
  @Override
  public void onSubTreeAdded(int i, LazyDiffTree<T> subtree) {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(subtree.name());
    JLazyDiffTree.initTreeNode(tree, childNode, subtree, onItemSelection);

    invokeLater(() -> {
      DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
      model.insertNodeInto(childNode, treeNode, i);
    });
  }

  @Override
  public void onSubTreeRemoved(int i) {
    // No need to remove listener, it will be garbage collected
    invokeLater(() -> {
      DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
      MutableTreeNode childNode = (MutableTreeNode) treeNode.getChildAt(i);
      model.removeNodeFromParent(childNode);
    });
  }

  public LazyDiffTree<T> innerTree() {
    return this.innerTree;
  }

  private void removePlaceholderNode() {
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    if (!treeNode.isLeaf()) {
      DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) treeNode.getFirstChild();
      // TODO: Better way to remove the default loading element
      if (firstNode != null && firstNode.getUserObject() instanceof String) {
        model.removeNodeFromParent(firstNode);
      }
    }
  }

  @Override
  public String toString() {
    return this.name;
  }
  
}
