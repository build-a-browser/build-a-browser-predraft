package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.util.Iterator;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;

public class ElementBoxIteratorImp implements ElementBoxIterator {

  private final AbstractElementBoxImp elementBox;

  private SinglyLinkedList<Box> prevPrevBox;
  private SinglyLinkedList<Box> prevBox;
  private SinglyLinkedList<Box> nextBox;
  private int curIndex = 0;

  public ElementBoxIteratorImp(AbstractElementBoxImp elementBox) {
    this.elementBox = elementBox;
    this.nextBox = elementBox.childBoxes;
  }

  public ElementBoxIteratorImp(ElementBoxIteratorImp ref) {
    this.elementBox = ref.elementBox;
    this.prevPrevBox = ref.prevPrevBox;
    this.prevBox = ref.prevBox;
    this.nextBox = ref.nextBox;
    this.curIndex = ref.curIndex;
  }

  @Override
  public Iterator<Box> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    return nextBox != null;
  }

  @Override
  public Box next() {
    prevPrevBox = prevBox;
    prevBox = nextBox;
    nextBox = nextBox.next();
    return prevBox.item();
  }

  @Override
  public boolean hasPrevious() {
    return prevBox != null;
  }

  @Override
  public Box previous() {
    throw new UnsupportedOperationException("Cannot call previous on an element box iterator!");
  }

  @Override
  public int nextIndex() {
    return curIndex;
  }

  @Override
  public int previousIndex() {
    return curIndex - 1;
  }

  @Override
  public void remove() {
    curIndex--;
    assert prevBox != prevPrevBox : "Cannot call remove twice!";
    assert prevBox != null : "Must call next before calling remove!";
    if (prevBox == elementBox.nextBox) {
      elementBox.nextBox = null;
    }
    if (prevPrevBox == null) {
      elementBox.childBoxes = prevBox.next();
      prevBox = null;
    } else {
      SinglyLinkedList.remove(prevPrevBox, 1);
      prevBox = prevPrevBox;
    }
  }

  @Override
  public void set(Box e) {
    SinglyLinkedList.replace(prevBox, 0, e);
  }

  @Override
  public void add(Box e) {
    curIndex++;
    prevPrevBox = prevBox;
    if (prevPrevBox != null) {
      SinglyLinkedList.insert(prevPrevBox, 1, e);
      prevBox = prevPrevBox.next();
    } else {
      prevBox = elementBox.childBoxes = SinglyLinkedList.insert(nextBox, 0, e);
    }

    if (elementBox.nextBox == prevPrevBox) {
      elementBox.nextBox = prevBox;
    }
  }

  @Override
  public ElementBoxIteratorImp clone() {
    return new ElementBoxIteratorImp(this);
  }

}
