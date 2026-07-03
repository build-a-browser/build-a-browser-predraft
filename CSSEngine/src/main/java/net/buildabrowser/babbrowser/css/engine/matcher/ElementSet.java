package net.buildabrowser.babbrowser.css.engine.matcher;

import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;

import com.zaxxer.sparsebits.SparseBitSet;

import net.buildabrowser.babbrowser.css.engine.matcher.imp.ElementRootSetImp;
import net.buildabrowser.babbrowser.dom.Element;

public interface ElementSet extends Iterable<Element> {
  
  boolean add(Element element);

  boolean addById(int elementId);

  boolean remove(Element element);

  boolean contains(Element element);

  boolean containsById(int elId);

  ElementSet copy();

  void intersect(ElementSet other);

  void union(ElementSet other);

  void difference(ElementSet other);

  void forEachElementId(IntConsumer iterator);

  ElementRootSet root();

  void resize(int size);

  boolean isEmpty();

  void removeAll();

  @Deprecated
  SparseBitSet raw();

  @Deprecated
  Set<Element> asSet();

  static ElementRootSet createRoot() {
    return new ElementRootSetImp(e -> {});
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
