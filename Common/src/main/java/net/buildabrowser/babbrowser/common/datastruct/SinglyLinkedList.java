package net.buildabrowser.babbrowser.common.datastruct;

import java.util.Iterator;
import java.util.List;

// Lighter than a linked list due to no wrapper and no prev pointer
public class SinglyLinkedList<T> {
  
  private T item;
  private SinglyLinkedList<T> nextNode;

  public T item() {
    return this.item;
  }

  public SinglyLinkedList<T> next() {
    return this.nextNode;
  }

  // Some convenience methods (for testing)
  public T get(int index) {
    SinglyLinkedList<T> curNode = this;
    for (int i = 0; i < index; i++) {
      curNode = curNode.nextNode;
    }
    return curNode.item;
  }

  // Real methods

  public static <T> SinglyLinkedList<T> insert(SinglyLinkedList<T> list, int i, T item) {
    SinglyLinkedList<T> newNode = new SinglyLinkedList<>();
    newNode.item = item;
    if (i == 0) {
      newNode.nextNode = list;
      return newNode;
    }

    SinglyLinkedList<T> curNode = list;
    for (int j = 0; j < i - 1; j++) {
      curNode = curNode.nextNode;
    }
    newNode.nextNode = curNode.nextNode;
    curNode.nextNode = newNode;

    return list;
  }

  public static <T> void replace(SinglyLinkedList<T> list, int i, T item) {
    if (i == 0) {
      list.item = item;
    }

    SinglyLinkedList<T> curNode = list;
    for (int j = 0; j < i; j++) {
      curNode = curNode.nextNode;
    }
    
    curNode.item = item;
  }

  public static <T> SinglyLinkedList<T> remove(SinglyLinkedList<T> list, int i) {
    if (i == 0) {
      return list.nextNode;
    }

    SinglyLinkedList<T> curNode = list;
    for (int j = 0; j < i - 1; j++) {
      curNode = curNode.nextNode;
    }
    curNode.nextNode = curNode.nextNode.nextNode;

    return list;
  }

  public static <T> T get(SinglyLinkedList<T> list, int i) {
    SinglyLinkedList<T> curNode = list;
    for (int j = 0; j < i; j++) {
      curNode = curNode.nextNode;
    }
    return curNode.item;
  }

  public static <T> SinglyLinkedList<T> add(SinglyLinkedList<T> list, T item) {
    SinglyLinkedList<T> newNode = new SinglyLinkedList<>();
    newNode.item = item;

    if (list == null) return newNode;
    
    lastNode(list).nextNode = newNode;
    return list;
  }

  public static <T> T last(SinglyLinkedList<T> list) {
    if (list == null) return null;

    return lastNode(list).item;
  }

  public static <T> SinglyLinkedList<T> removeLast(SinglyLinkedList<T> list) {
    if (list == null || list.nextNode == null) {
      return null;
    }

    SinglyLinkedList<T> curNode = list;
    while (curNode.nextNode.nextNode != null) {
      curNode = curNode.nextNode;
    }

    curNode.nextNode = null;

    return list;
  }

  public static <T> SinglyLinkedList<T> lastNode(SinglyLinkedList<T> list) {
    if (list == null) return null;

    SinglyLinkedList<T> curNode = list;
    while (curNode.nextNode != null) {
      curNode = curNode.nextNode;
    }

    return curNode;
  }

  public static <T> SinglyLinkedList<T> fromList(List<T> fragments) {
    if (fragments.isEmpty()) return null;
    Iterator<T> it = fragments.iterator();
    assert it.hasNext();
    SinglyLinkedList<T> list = new SinglyLinkedList<>();
    SinglyLinkedList<T> curNode = list;
    curNode.item = it.next();

    while (it.hasNext()) {
      curNode.nextNode = new SinglyLinkedList<>();
      curNode = curNode.nextNode;
      curNode.item = it.next();
    }

    return list;
  }

  
  public static int _testingOnlySize(SinglyLinkedList<?> curNode) {
    int calcSize = 0;
    while (curNode != null) {
      calcSize++;
      curNode = curNode.next();
    }
    return calcSize;
  }

}
