package net.buildabrowser.babbrowser.css.engine.matcher;

import java.util.List;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.matcher.imp.ElementSetImp;
import net.buildabrowser.babbrowser.dom.Element;

public interface ElementSet extends Iterable<Element> {
  
  void add(Element element);

  void remove(Element element);

  boolean contains(Element element);

  ElementSet copy();

  void intersect(ElementSet other);

  void union(ElementSet other);

  @Deprecated
  Set<Element> raw();

  static ElementSet create() {
    return new ElementSetImp();
  }

  static ElementSet unionMany(List<ElementSet> sets) {
    ElementSet base = sets.get(0).copy();
    for (int i = 1; i < sets.size(); i++) {
      base.union(sets.get(i));
    }

    return base;
  }

  static ElementSet intersectMany(List<ElementSet> sets) {
    ElementSet base = sets.get(0).copy();
    for (int i = 1; i < sets.size(); i++) {
      base.intersect(sets.get(i));
    }

    return base;
  }

}
