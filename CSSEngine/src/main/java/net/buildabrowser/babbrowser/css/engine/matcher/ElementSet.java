package net.buildabrowser.babbrowser.css.engine.matcher;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.zaxxer.sparsebits.SparseBitSet;

import net.buildabrowser.babbrowser.css.engine.matcher.imp.ElementRootSetImp;
import net.buildabrowser.babbrowser.dom.Element;

public interface ElementSet extends Iterable<Element> {
  
  boolean add(Element element);

  boolean remove(Element element);

  boolean contains(Element element);

  ElementSet copy();

  void intersect(ElementSet other);

  void union(ElementSet other);

  ElementRootSet root();

  void markChanged(Element element);

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

  static ElementRootSet createRoot(Consumer<Element> changeListener) {
    return new ElementRootSetImp(changeListener);
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
