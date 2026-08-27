package net.buildabrowser.babbrowser.debugger.swing;

import java.util.List;

import net.buildabrowser.babbrowser.debugger.swing.imp.LazyDiffTreeImp;

public interface LazyDiffTree<T> {
  
  String name();

  void open();

  void openNow();

  void close(boolean isUISource);

  void select();

  T object();

  void update(T newObject);

  void attachListener(LazyDiffTreeListener<T> listener);

  void removeListener(LazyDiffTreeListener<T> listener);

  boolean isLeaf();

  boolean isOpen();

  LazyDiffTree<T> child(T object);

  default void update() {
    update(object());
  }

  interface LazyDiffTreeListener<T> {

    void onNameChanged(String newName);

    void onOpening();

    void onOpened();

    void onClosed(boolean isUISource);

    void onSelect();

    void onSubTreeAdded(int i, LazyDiffTree<T> tree);

    void onSubTreeRemoved(int i);

  }

  interface TreeOps<T> {

    String name(T node);

    List<T> children(T node);

    boolean isNodeIgnored(T node);

    boolean isNodeLeaf(T node);

  }

  static <T> LazyDiffTree<T> create(TreeOps<T> treeOps) {
    return new LazyDiffTreeImp<>(treeOps, null);
  }

}
