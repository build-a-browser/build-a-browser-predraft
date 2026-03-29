package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Element;

public class ElementSetIteratorImp implements Iterator<Element> {

  private final List<Element> elementList;
  private final BitSet rawList;

  private int nextIndex = 0;

  public ElementSetIteratorImp(
    List<Element> elementList,
    BitSet rawList
  ) {
    this.elementList = elementList;
    this.rawList = rawList;
  }

  @Override
  public boolean hasNext() {
    findValid();
    return
      nextIndex < elementList.size()
      && nextIndex != -1;
  }

  @Override
  public Element next() {
    findValid();
    return elementList.get(nextIndex++);
  }

  private void findValid() {
    if (nextIndex < 0) return;
    nextIndex = rawList.nextSetBit(nextIndex);
  }

}
