package net.buildabrowser.babbrowser.common.datastruct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

// Lighter than a linked list due to no wrapper and no prev pointer
public interface IntrusiveList<T extends IntrusiveList<T>> {
  
  T next();

  void setNext(T nextNode);

  // Some convenience methods (for testing)
  @SuppressWarnings("unchecked")
  default T get(int index) {
    IntrusiveList<T> curNode = this;
    for (int i = 0; i < index; i++) {
      curNode = curNode.next();
    }
    return (T) curNode;
  }

  // Real methods

  public static <T extends IntrusiveList<T>> T insert(T list, int i, T item) {
    if (i == 0) {
      item.setNext(list);
      return item;
    }

    T curNode = list;
    for (int j = 0; j < i - 1; j++) {
      curNode = curNode.next();
    }
    item.setNext(curNode.next());
    curNode.setNext(item);

    return list;
  }

  public static <T extends IntrusiveList<T>> T push(T list, T item) {
    item.setNext(list);
    return item;
  }

  // TODO: Return
  public static <T extends IntrusiveList<T>> T replace(T list, int i, T item) {
    if (i == 0) {
      assert list != null;
      item.setNext(list.next());
      return item;
    }

    T curNode = list;
    for (int j = 0; j < i - 1; j++) {
      curNode = curNode.next();
    }
    
    assert curNode.next() != null;
    item.setNext(curNode.next().next());
    curNode.setNext(item);
    return list;
  }

  public static <T extends IntrusiveList<T>> T remove(T list, int i) {
    if (i == 0) {
      return list.next();
    }

    T curNode = list;
    for (int j = 0; j < i - 1; j++) {
      curNode = curNode.next();
    }
    T oldNode = curNode.next();
    curNode.setNext(curNode.next().next());
    oldNode.setNext(null);

    return list;
  }

  public static <T extends IntrusiveList<T>> T get(T list, int i) {
    T curNode = list;
    for (int j = 0; j < i; j++) {
      curNode = curNode.next();
    }
    return curNode;
  }

  public static <T extends IntrusiveList<T>> T add(T list, T item) {
    item.setNext(null);
    if (list == null) return item;
    
    last(list).setNext(item);
    return list;
  }

  public static <T extends IntrusiveList<T>> T last(T list) {
    if (list == null) return null;

    T curNode = list;
    while (curNode.next() != null) {
      curNode = curNode.next();
    }

    return curNode;
  }

  public static <T extends IntrusiveList<T>> T removeLast(T list) {
    if (list == null || list.next() == null) {
      return null;
    }

    T curNode = list;
    while (curNode.next().next() != null) {
      curNode = curNode.next();
    }

    curNode.setNext(null);

    return list;
  }

  public static <T extends IntrusiveList<T>> T sort(T list, Comparator<? super T> comparator) {
    if (list == null) return null;
    // TODO: This is the easy way to do things, but look into sorting without a copy
    List<T> toSort = toList(list);
    toSort.sort(comparator);
    return fromList(toSort);
  }

  public static <T extends IntrusiveList<T>> void forEach(T item, Consumer<T> func) {
    T nextItem = item;
    while (nextItem != null) {
      func.accept(nextItem);
      nextItem = nextItem.next();
    }
  }

  static <T extends IntrusiveList<T>> List<T> toList(T list) {
    List<T> asList = new ArrayList<>(_testingOnlySize(list));
    T current = list;
    while (current != null) {
      asList.add(current);
      current = current.next();
    }

    return asList;
  }

  public static <T extends IntrusiveList<T>> T fromList(List<T> list) {
    if (list.isEmpty()) return null;
    Iterator<T> it = list.iterator();
    assert it.hasNext();
    T asList = it.next();
    T curNode = asList;

    while (it.hasNext()) {
      T nextNode = it.next();
      nextNode.setNext(null);
      curNode.setNext(nextNode);
      curNode = nextNode;
    }
    curNode.setNext(null);

    return asList;
  }
  
  public static int _testingOnlySize(IntrusiveList<?> curNode) {
    int calcSize = 0;
    while (curNode != null) {
      calcSize++;
      curNode = curNode.next();
    }
    return calcSize;
  }

  public static boolean _ensureNoLoops(IntrusiveList<?> list) {
    List<IntrusiveList<?>> seenNodes = new ArrayList<>();
    IntrusiveList<?> curNode = list;
    while (curNode != null) {
      if (seenNodes.contains(curNode)) {
        return false;
      }
      seenNodes.add(curNode);
      curNode = curNode.next();
    }

    return true;
  }

}
