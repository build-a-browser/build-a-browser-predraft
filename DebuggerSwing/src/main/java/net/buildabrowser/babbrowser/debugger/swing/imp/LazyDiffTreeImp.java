package net.buildabrowser.babbrowser.debugger.swing.imp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree;

// TODO: Synchronize 
public class LazyDiffTreeImp<T> implements LazyDiffTree<T> {

  private final List<LazyDiffTreeListener<T>> listeners = new ArrayList<>(1);
  private final List<LazyDiffTree<T>> children = new ArrayList<>(4);
  private final List<Runnable> taskQueue = new LinkedList<>();

  private final TreeOps<T> ops;

  private String oldName;
  private T object;
  private boolean opened;

  public LazyDiffTreeImp(
    TreeOps<T> ops,
    T initValue
  ) {
    this.ops = ops;
    this.object = initValue;
  }

  @Override
  public String name() {
    if (object == null) return "<???>";
    return ops.name(object);
  }

  @Override
  public void open() {
    forEachListener(l -> l.onOpening());
    queueUpdateTask(() -> {
      this.opened = true;
      forEachListener(l -> l.onOpened());
    });
  }

  @Override
  public void openNow() {
    open();
    runQueuedTasks();
    rediff();
  }

  @Override
  public void close(boolean isUISource) {
    this.opened = false;
    children.clear();
    forEachListener(l -> l.onClosed(isUISource));
  }

  @Override
  public void select() {
    queueUpdateTask(() -> {
      forEachListener(l -> l.onSelect());
    });
  }

  @Override
  public T object() {
    return this.object;
  }

  @Override
  public void update(T newObject) {
    String currentName = ops.name(newObject);
    if (!currentName.equals(oldName)) {
      this.oldName = currentName;
      forEachListener(l -> l.onNameChanged(currentName));
    }
    this.object = newObject;
    runQueuedTasks();
    rediff();
  }

  @Override
  public void attachListener(LazyDiffTreeListener<T> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(LazyDiffTreeListener<T> listener) {
    listeners.remove(listener);
  }

  @Override
  public boolean isLeaf() {
    return object != null && ops.isNodeLeaf(object);
  }

  @Override
  public boolean isOpen() {
    return this.opened;
  }

  @Override
  public LazyDiffTree<T> child(T object) {
    for (LazyDiffTree<T> child: children) {
      if (child.object() == object) {
        return child;
      }
    }

    return null;
  }

  private void forEachListener(
    Consumer<LazyDiffTreeListener<T>> listenerFunc
  ) {
    for (LazyDiffTreeListener<T> listener: listeners) {
      listenerFunc.accept(listener);
    }
  }

  private void queueUpdateTask(Runnable task) {
    taskQueue.add(task);
  }

  private void runQueuedTasks() {
    while (!taskQueue.isEmpty()) {
      taskQueue.remove(0).run();
    }
  }

  private void rediff() {
    if (!this.opened) return;

    int insertIndex = 0;
    for (T child: ops.children(object)) {
      if (ops.isNodeIgnored(child)) continue;

      int nextMatchIndex = findMatch(child, insertIndex);
      if (nextMatchIndex != -1) {
        removeOldChildren(
          insertIndex,
          nextMatchIndex - insertIndex);
        children.get(insertIndex).update(child);
      } else {
        LazyDiffTree<T> subtree = new LazyDiffTreeImp<>(ops, child);
        children.add(insertIndex, subtree);
        int insertIndex_ = insertIndex;
        forEachListener(l -> l.onSubTreeAdded(insertIndex_, subtree));
      }
      insertIndex++;
    }

    removeOldChildren(insertIndex, children.size() - insertIndex);
  }

  private int findMatch(T child, int insertIndex) {
    for (int i = insertIndex; i < children.size(); i++) {
      if (children.get(i).object().equals(child)) {
        return i;
      }
    }

    return -1;
  }

  private void removeOldChildren(int index, int run) {
    for (int i = 0; i < run; i++) {
      children.remove(index);
      forEachListener(l -> l.onSubTreeRemoved(index));
    }
  }

}
