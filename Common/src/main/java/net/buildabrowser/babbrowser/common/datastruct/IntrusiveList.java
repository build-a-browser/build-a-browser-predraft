package net.buildabrowser.babbrowser.common.datastruct;

import java.util.Iterator;
import java.util.List;

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
    curNode.setNext(curNode.next().next());

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

  public static <T extends IntrusiveList<T>> T fromList(List<T> fragments) {
    if (fragments.isEmpty()) return null;
    Iterator<T> it = fragments.iterator();
    assert it.hasNext();
    T list = it.next();
    T curNode = list;

    while (it.hasNext()) {
      curNode.setNext(it.next());
      curNode = curNode.next();
    }
    curNode.setNext(null);

    return list;
  }

  
  public static int _testingOnlySize(IntrusiveList<?> curNode) {
    int calcSize = 0;
    while (curNode != null) {
      calcSize++;
      curNode = curNode.next();
    }
    return calcSize;
  }

}
