package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.dom.Element;

public class ElementRootSetImp extends ElementSetImp implements ElementRootSet {

  private static final int NUM_INITIAL_ELEMENTS = 128;

  // TODO: Periodically remove old entries
  private final LinkedList<WeakReference<ElementSet>> childSets = new LinkedList<>();

  private Runnable onChange;
  private int nextId = 0;

  public ElementRootSetImp() {
    super(
      null,
      new ArrayList<>(NUM_INITIAL_ELEMENTS),
      new BitSet(NUM_INITIAL_ELEMENTS));
  }

  @Override
  public ElementSet createChild() {
    ElementSet set = new ElementSetImp(this, elementList, rawSet.size());
    childSets.add(new WeakReference<>(set));
    return set;
  }

  @Override
  public ElementSet createUntrackedChild() {
    ElementSet set = new ElementSetImp(this, elementList, rawSet.size()) {
      @Override
      public void markChanged() {}
    };
    childSets.add(new WeakReference<>(set));
    return set;
  }

  @Override
  public ElementSet createTemporaryChild() {
    return new ElementSetImp(this, elementList, rawSet.size()) {
      @Override
      public void markChanged() {}
    };
  }

  @Override
  public void add(Element element) {
    resizeChildrenIfNeeded();
    assignElementId(element);
    
    rawSet.set(element.getId(), true);
  }

  @Override
  public ElementRootSet root() {
    return this;
  }

  @Override
  public void markChanged() {
    if (this.onChange == null) return;
    this.onChange.run();
  }

  @Override
  public void attachChangeListener(Runnable changeListener) {
    this.onChange = changeListener;
  }

  private void resizeChildrenIfNeeded() {
    if (nextId > rawSet.size()) {
      int newSize = rawSet.size() * 2;
      for (WeakReference<ElementSet> childSet: childSets) {
        childSet.get().resize(newSize);
      }
      resize(newSize);
    }
  }

  private void assignElementId(Element element) {
    if (nextId == elementList.size()) {
      elementList.add(element);
    } else {
      elementList.set(nextId, element);
    }
    element.setId(nextId++);
  }

  // TODO: Add option to compact, in case many nodes are removed

}
