package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;

public class ElementSetImp implements ElementSet {

  private final Set<Element> rawSet;

  public ElementSetImp() {
    this.rawSet = new HashSet<>();
  }

  private ElementSetImp(Set<Element> set) {
    this.rawSet = set;
  }

  @Override
  public Iterator<Element> iterator() {
    return rawSet.iterator();
  }

  @Override
  public void add(Element element) {
    rawSet.add(element);
  }

  @Override
  public void remove(Element element) {
    rawSet.remove(element);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void intersect(ElementSet other) {
    rawSet.retainAll(other.raw());
  }

  @Override
  @SuppressWarnings("deprecation")
  public void union(ElementSet other) {
    rawSet.addAll(other.raw());
  }

  @Override
  public Set<Element> raw() {
    return rawSet;
  }

  @Override
  public ElementSet clone() {
    Set<Element> newSet = new HashSet<>();
    newSet.addAll(rawSet);
    return new ElementSetImp(newSet);
  }
  
}
