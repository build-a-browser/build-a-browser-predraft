package net.buildabrowser.babbrowser.common.datastruct;

// Lighter than a linked list due to no wrapper and no prev pointer
public class SinglyLinkedList<T> {
  
  private T item;
  private SinglyLinkedList<T> nextNode;

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

}
