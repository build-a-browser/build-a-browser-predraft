package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zaxxer.sparsebits.SparseBitSet;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;

public class ElementSetImp implements ElementSet {

  private final ElementRootSet root;

  protected final List<Element> elementList;

  protected SparseBitSet rawSet;

  public ElementSetImp(
    ElementRootSet root, List<Element> elementList, int numElements
  ) {
    this.root = root;
    this.elementList = elementList;
    this.rawSet = new SparseBitSet(numElements);
  }

  // Not updated on DOM update, as it is only used in intermediate calculations
  public ElementSetImp(ElementRootSet root, List<Element> elementList, SparseBitSet rawSet) {
    this.root = root;
    this.elementList = elementList;
    this.rawSet = rawSet;
  }

  @Override
  public Iterator<Element> iterator() {
    return new ElementSetIteratorImp(elementList, rawSet);
  }

  @Override
  public boolean add(Element element) {
    if (!rawSet.get(element.getId())) {
      rawSet.set(element.getId(), true);
      markChanged(element);
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(Element element) {
    if (rawSet.get(element.getId())) {
      markChanged(element);
      rawSet.set(element.getId(), false);
      return true;
    }
    return false;
  }

  @Override
  public boolean contains(Element element) {
    return rawSet.get(element.getId());
  }

  @Override
  @SuppressWarnings("deprecation")
  public void intersect(ElementSet other) {
    rawSet.and(other.raw());
  }

  @Override
  @SuppressWarnings("deprecation")
  public void union(ElementSet other) {
    rawSet.or(other.raw());
  }

  @Override
  public ElementRootSet root() {
    return this.root;
  }

  @Override
  public void markChanged(Element element) {
    root().markChanged(element);
  }

  @Override
  public void resize(int size) {
    // SparseBitSet library does this automatically
  }

  @Override
  public boolean isEmpty() {
    return rawSet.isEmpty();
  }

  @Override
  public void removeAll() {
    rawSet.clear();
  }

  @Override
  public SparseBitSet raw() {
    return rawSet;
  }

  @Override
  public Set<Element> asSet() {
    Iterator<Element> it = iterator();
    Set<Element> set = new HashSet<>();
    while (it.hasNext()) {
      set.add(it.next());
    }

    return set;
  }

  @Override
  public ElementSet copy() {
    SparseBitSet newSet = (SparseBitSet) rawSet.clone();
    return new ElementSetImp(root(), elementList, newSet);
  }
  
}
