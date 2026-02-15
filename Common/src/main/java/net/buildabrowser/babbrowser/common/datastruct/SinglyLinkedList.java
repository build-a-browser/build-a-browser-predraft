package net.buildabrowser.babbrowser.common.datastruct;

public class SinglyLinkedList<T> implements IntrusiveList<SinglyLinkedList<T>> {

  private final T item;
  private SinglyLinkedList<T> nextItem;

  public SinglyLinkedList(T item) {
    this.item = item;
  }

  @Override
  public SinglyLinkedList<T> next() {
    return this.nextItem;
  }

  @Override
  public void setNext(SinglyLinkedList<T> nextNode) {
    this.nextItem = nextNode;
  }

  public T item() {
    return this.item;
  }
  
}
