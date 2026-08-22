package net.buildabrowser.babbrowser.debugger.swing.gui;

import java.util.function.Consumer;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree;

public final class JLazyDiffTree {

  private JLazyDiffTree() {}

  public static <T> JTree createJLazyDiffTree(
    LazyDiffTree<T> innerTree,
    Consumer<T> onItemSelection
  ) {
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(innerTree.name());
    JTree tree = new JTree(rootNode);
    ((DefaultTreeModel) tree.getModel()).setAsksAllowsChildren(true);
    initTreeNode(tree, rootNode, innerTree);

    tree.addTreeExpansionListener(new TreeExpansionListener() {

      @Override
      public void treeExpanded(TreeExpansionEvent event) {
        DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        JLazyDiffTreeListener<?> listener = (JLazyDiffTreeListener<?>) sourceNode.getUserObject();
        listener.innerTree().open();
      }

      @Override
      public void treeCollapsed(TreeExpansionEvent event) {
        DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        JLazyDiffTreeListener<?> listener = (JLazyDiffTreeListener<?>) sourceNode.getUserObject();
        listener.innerTree().close(true);
      }
      
    });

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      @SuppressWarnings("unchecked")
      public void valueChanged(TreeSelectionEvent event) {
        DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        JLazyDiffTreeListener<?> listener = (JLazyDiffTreeListener<?>) sourceNode.getUserObject();
        onItemSelection.accept((T) listener.innerTree().object());
      }
      
    });

    return tree;
  }

  public static <T> void initTreeNode(
    JTree tree,
    DefaultMutableTreeNode node,
    LazyDiffTree<T> innerTree
  ) {
    JLazyDiffTreeListener<T> rootNodeListener = new JLazyDiffTreeListener<>(tree, node, innerTree);
    innerTree.attachListener(rootNodeListener);
    node.setUserObject(rootNodeListener);
    prepareTreeNodeForChildren(node, innerTree);
  }

  public static <T> void prepareTreeNodeForChildren(
    DefaultMutableTreeNode node,
    LazyDiffTree<T> innerTree
  ) {
    if (!innerTree.isLeaf()) {
      node.setAllowsChildren(true);
      DefaultMutableTreeNode loadNode = new DefaultMutableTreeNode("Loading...");
      loadNode.setAllowsChildren(false);
      node.add(loadNode);
    } else {
      node.setAllowsChildren(false);
    }
  }

}
